import requests
import mysql.connector
import os
import time
import sys
from datetime import datetime
from googletrans import Translator
from dotenv import load_dotenv
import re  # Vẫn giữ reเผื่อ có trường hợp cần thiết, dù TheMealDB ít HTML hơn

# Lấy đường dẫn thư mục hiện tại của file Python
current_dir = os.path.dirname(os.path.abspath(__file__))
env_path = os.path.join(current_dir, '.env')

# Load environment variables from .env file
load_dotenv(env_path)

# --- Configuration ---
THEMEALDB_API_KEY = os.getenv('THEMEALDB_API_KEY', '1')  # '1' là key công cộng
THEMEALDB_BASE_URL = f'https://www.themealdb.com/api/json/v1/{THEMEALDB_API_KEY}'

DB_HOST = os.getenv('DB_HOST', 'localhost')
DB_USER = os.getenv('DB_USER')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_NAME = os.getenv('DB_NAME')

if THEMEALDB_API_KEY == '1':
    print("Thông báo: Sử dụng API Key công cộng '1' cho TheMealDB. "
          "Để có key riêng và hỗ trợ dự án, bạn có thể xem xét việc trở thành Patreon của họ.")
if not DB_USER or not DB_PASSWORD or not DB_NAME:
    raise ValueError("Vui lòng cấu hình đầy đủ thông tin database trong file .env (DB_USER, DB_PASSWORD, DB_NAME)")

# Initialize Translator
translator = Translator()


# --- Helper Functions ---

def translate_text(text, dest_lang='vi'):
    """
    Dịch văn bản sang ngôn ngữ đích, xử lý lỗi cơ bản và fallback về văn bản gốc.
    Luôn trả về một chuỗi, kể cả chuỗi rỗng nếu đầu vào không hợp lệ.
    """
    if text is None:
        return ""
    original_text = str(text).strip()

    if not original_text:
        return ""

    try:
        translated = translator.translate(original_text, dest=dest_lang)

        if not translated or not translated.text or not translated.text.strip():
            print(f"Cảnh báo: Dịch thuật cho '{original_text}' trả về kết quả rỗng. Sử dụng văn bản gốc.")
            return original_text

        translated_text_clean = translated.text.strip()

        # Kịch bản 1: Dịch thành công rõ rệt (nguồn khác đích VÀ văn bản thay đổi)
        if translated.src != dest_lang and original_text.lower() != translated_text_clean.lower():
            print(f"Đã dịch: '{original_text}' ({translated.src}) -> '{translated_text_clean}' ({dest_lang})")
            time.sleep(0.5)
            return translated_text_clean
        # Kịch bản 2: Ngôn ngữ nguồn được phát hiện là ngôn ngữ đích (src == dest_lang)
        elif translated.src == dest_lang:
            print(
                f"Thông tin: Văn bản '{original_text}' (src={translated.src}) -> '{translated_text_clean}' (dest={dest_lang}). "
                f"Ngôn ngữ nguồn có thể đã là đích hoặc dịch không thay đổi nhiều.")
            return translated_text_clean  # Trả về bản dịch (có thể đã được chuẩn hóa bởi googletrans)
        # Kịch bản 3: Nguồn khác đích, nhưng văn bản không thay đổi (tên riêng, từ không dịch được)
        elif original_text.lower() == translated_text_clean.lower():
            print(
                f"Thông tin: Văn bản '{original_text}' ({translated.src}) không thay đổi sau khi dịch sang {dest_lang} ('{translated_text_clean}'). "
                f"Có thể là tên riêng. Sử dụng kết quả (có thể đã chuẩn hóa).")
            return translated_text_clean
        # Kịch bản Fallback
        else:
            print(
                f"Thông tin (Fallback): Văn bản '{original_text}' ({translated.src}) -> '{translated_text_clean}' ({dest_lang}). Sử dụng kết quả dịch.")
            time.sleep(0.5)
            return translated_text_clean
    except Exception as e:
        print(f"Lỗi dịch thuật cho '{original_text}': {e}. Sử dụng văn bản gốc.")
        return original_text


def get_db_connection():
    """Kết nối tới cơ sở dữ liệu MySQL."""
    try:
        conn = mysql.connector.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME
        )
        print("Kết nối Database thành công!")
        return conn
    except mysql.connector.Error as err:
        print(f"Lỗi kết nối Database: {err}")
        return None


def get_or_create_ingredient(cursor, ingredient_name_en_original):
    """
    Kiểm tra xem nguyên liệu đã tồn tại (dựa trên tên tiếng Việt đã được chuẩn hóa) chưa.
    Nếu chưa, dịch tên, thêm vào DB và trả về (ID, tên tiếng Việt).
    Nếu đã tồn tại, trả về (ID hiện có, tên tiếng Việt).
    Trả về (None, None) nếu có lỗi hoặc tên nguyên liệu không hợp lệ.
    """
    ingredient_name_en_standardized = ingredient_name_en_original.strip().capitalize()

    if not ingredient_name_en_standardized:
        print(f"Cảnh báo: Tên nguyên liệu gốc '{ingredient_name_en_original}' rỗng sau chuẩn hóa. Bỏ qua.")
        return None, None

    ingredient_name_vi = translate_text(ingredient_name_en_standardized)

    if not ingredient_name_vi:
        print(f"Cảnh báo: Tên nguyên liệu tiếng Việt rỗng sau khi dịch từ '{ingredient_name_en_standardized}'. Bỏ qua.")
        return None, None

    cursor.execute("SELECT id FROM ingredients WHERE name = %s", (ingredient_name_vi,))
    result = cursor.fetchone()

    if result:
        return result[0], ingredient_name_vi
    else:
        try:
            icon_placeholder = None  # TheMealDB không cung cấp icon cho nguyên liệu riêng lẻ
            cursor.execute(
                "INSERT INTO ingredients (name, icon) VALUES (%s, %s)",
                (ingredient_name_vi, icon_placeholder)
            )
            ingredient_id = cursor.lastrowid
            print(
                f"Đã thêm nguyên liệu mới: '{ingredient_name_vi}' (từ '{ingredient_name_en_standardized}') với ID: {ingredient_id}")
            return ingredient_id, ingredient_name_vi
        except mysql.connector.Error as err:
            print(f"Lỗi khi thêm nguyên liệu '{ingredient_name_vi}' (từ '{ingredient_name_en_standardized}'): {err}")
            return None, None


# --- Main Data Fetching and Storing Logic ---

def fetch_and_store_recipes_themealdb(num_recipes=10, query=""):
    """
    Tìm kiếm công thức trên TheMealDB, lấy chi tiết và lưu vào DB.
    num_recipes: Số lượng công thức muốn lấy.
    query: Từ khóa tìm kiếm (tên món ăn). Nếu để trống, sẽ lấy ngẫu nhiên.
    """
    conn = get_db_connection()
    if not conn:
        return

    cursor = conn.cursor()
    meals_to_process = []  # Danh sách các đối tượng 'meal' từ API để xử lý

    if query:
        search_url = f"{THEMEALDB_BASE_URL}/search.php"
        params = {'s': query}
        print(f"Đang tìm kiếm công thức với query='{query}' trên TheMealDB...")
        try:
            response = requests.get(search_url, params=params)
            response.raise_for_status()
            data = response.json()
            if data and data.get('meals'):  # API trả về {'meals': null} nếu không tìm thấy
                meals_to_process = data['meals'][:num_recipes]  # Lấy tối đa num_recipes kết quả
                print(f"Tìm thấy {len(data['meals'])} công thức, sẽ xử lý tối đa {len(meals_to_process)} công thức.")
            else:
                print(f"Không tìm thấy công thức nào với query='{query}'.")
        except requests.exceptions.RequestException as e:
            print(f"Lỗi khi gọi API tìm kiếm TheMealDB: {e}")
        except ValueError as e:  # Lỗi parse JSON
            print(f"Lỗi khi phân tích JSON từ API tìm kiếm TheMealDB: {e}")
            if 'response' in locals() and response: print(f"Response text: {response.text}")
    else:  # Không có query, lấy ngẫu nhiên
        print(f"Đang lấy {num_recipes} công thức ngẫu nhiên từ TheMealDB...")
        random_url = f"{THEMEALDB_BASE_URL}/random.php"
        for i in range(num_recipes):
            try:
                # Thêm độ trễ nhỏ giữa các lần gọi API ngẫu nhiên
                if i > 0: time.sleep(0.5)  # TheMealDB thường không quá khắt khe

                response_random = requests.get(random_url)
                response_random.raise_for_status()
                data_random = response_random.json()
                if data_random and data_random.get('meals') and data_random['meals'][0]:
                    meal_data = data_random['meals'][0]
                    meals_to_process.append(meal_data)
                    print(f"Đã lấy công thức ngẫu nhiên {i + 1}/{num_recipes}: {meal_data.get('strMeal')}")
                else:
                    print(f"Không lấy được công thức ngẫu nhiên lần thứ {i + 1}.")
            except requests.exceptions.RequestException as e:
                print(f"Lỗi khi gọi API lấy công thức ngẫu nhiên TheMealDB (lần {i + 1}): {e}")
            except ValueError as e:
                print(f"Lỗi khi phân tích JSON từ API ngẫu nhiên TheMealDB (lần {i + 1}): {e}")
                if 'response_random' in locals() and response_random: print(f"Response text: {response_random.text}")
            except Exception as ex:
                print(f"Lỗi không xác định khi lấy công thức ngẫu nhiên (lần {i + 1}): {ex}")

    if not meals_to_process:
        print("Không có dữ liệu công thức để xử lý.")
        if conn: conn.close()
        return

    processed_count = 0
    for meal_details in meals_to_process:
        meal_id_api = meal_details.get('idMeal')
        recipe_name_en = meal_details.get('strMeal')

        if not recipe_name_en:  # Kiểm tra cơ bản
            print(f"Cảnh báo: Công thức từ API (ID: {meal_id_api}) không có tên. Bỏ qua.")
            continue

        print(f"\n--- Đang xử lý công thức: '{recipe_name_en}' (ID API: {meal_id_api}) ---")

        recipe_description_en = meal_details.get('strInstructions', '')
        recipe_image = meal_details.get('strMealThumb')

        # Trích xuất danh sách nguyên liệu từ TheMealDB
        ingredients_en_objects = []
        for i in range(1, 21):  # TheMealDB có từ strIngredient1 đến strIngredient20
            ingredient_name = meal_details.get(f'strIngredient{i}')
            # measure = meal_details.get(f'strMeasure{i}') # Thông tin định lượng, chưa dùng
            if ingredient_name and ingredient_name.strip():
                ingredients_en_objects.append({'name': ingredient_name.strip()})
            else:
                break  # Dừng nếu không còn tên nguyên liệu

        recipe_name_vi = translate_text(recipe_name_en)
        recipe_description_vi = translate_text(recipe_description_en)

        if not recipe_name_vi:  # Đảm bảo tên công thức không rỗng sau dịch
            print(f"Cảnh báo: Tên công thức '{recipe_name_en}' bị rỗng sau dịch. Sử dụng tên gốc.")
            recipe_name_vi = recipe_name_en.strip()
            if not recipe_name_vi:
                print(f"Cảnh báo: Tên công thức gốc cũng rỗng cho ID API {meal_id_api}. Bỏ qua.")
                continue

        try:
            cursor.execute(
                "INSERT INTO recipes (name, description, image) VALUES (%s, %s, %s)",
                (recipe_name_vi, recipe_description_vi, recipe_image)
            )
            new_recipe_db_id = cursor.lastrowid
            print(f"Đã lưu công thức '{recipe_name_vi}' với ID DB: {new_recipe_db_id}")

            if ingredients_en_objects:
                for ing_obj in ingredients_en_objects:
                    ingredient_name_en_original = ing_obj.get('name')
                    if ingredient_name_en_original:  # Đã được strip() ở trên
                        db_ingredient_id, ingredient_name_vi_final = get_or_create_ingredient(cursor,
                                                                                              ingredient_name_en_original)

                        if db_ingredient_id and ingredient_name_vi_final is not None:
                            try:
                                cursor.execute(
                                    "INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (%s, %s)",
                                    (new_recipe_db_id, db_ingredient_id)
                                )
                                # Tên tiếng Anh đã được capitalize trong get_or_create_ingredient để log
                                standardized_en_name_for_log = ingredient_name_en_original.strip().capitalize()
                                print(
                                    f"  -> Đã liên kết CT {new_recipe_db_id} với NL ID {db_ingredient_id} ('{standardized_en_name_for_log}' -> '{ingredient_name_vi_final}')")
                            except mysql.connector.Error as err:
                                if err.errno == 1062:  # Mã lỗi cho duplicate entry (nếu có UNIQUE constraint)
                                    print(
                                        f"  -> Liên kết CT {new_recipe_db_id} với NL ID {db_ingredient_id} ('{ingredient_name_vi_final}') đã tồn tại.")
                                else:
                                    print(
                                        f"  -> Lỗi khi liên kết CT {new_recipe_db_id} với NL ID {db_ingredient_id} ('{ingredient_name_vi_final}'): {err}")
                        else:
                            print(
                                f"  -> Bỏ qua liên kết cho nguyên liệu không hợp lệ: '{ingredient_name_en_original}' (Công thức '{recipe_name_en}')")

            conn.commit()
            processed_count += 1
            print(
                f"--- Hoàn tất xử lý công thức '{recipe_name_en}' (ID API: {meal_id_api}, ID DB: {new_recipe_db_id}) ---")

        except mysql.connector.Error as err:
            print(f"Lỗi khi lưu công thức '{recipe_name_vi}' (ID API: {meal_id_api}): {err}")
            conn.rollback()

    if cursor: cursor.close()
    if conn: conn.close()
    print(f"\nHoàn thành! Đã xử lý và lưu thành công {processed_count} công thức từ TheMealDB.")


def main():
    # Lấy số lượng công thức từ tham số dòng lệnh
    num_recipes = 10  # Giá trị mặc định
    if len(sys.argv) > 1:
        try:
            num_recipes = int(sys.argv[1])
        except ValueError:
            print("Số lượng công thức không hợp lệ, sử dụng giá trị mặc định: 10")

    print(f"Bắt đầu lấy {num_recipes} công thức nấu ăn...")
    start_time = time.time()
    start_datetime = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"Thời gian bắt đầu: {start_datetime}")

    try:
        fetch_and_store_recipes_themealdb(num_recipes)
    except Exception as e:
        print(f"Lỗi không mong muốn: {e}")
    finally:
        end_time = time.time()
        end_datetime = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        duration = end_time - start_time
        print(f"\nThời gian kết thúc: {end_datetime}")
        print(f"Tổng thời gian chạy: {duration:.2f} giây")


# --- Chạy Script ---
if __name__ == "__main__":
    main()