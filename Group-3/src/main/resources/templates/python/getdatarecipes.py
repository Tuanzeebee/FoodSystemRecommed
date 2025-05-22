import requests
import mysql.connector
import os
import time
import sys
from datetime import datetime
from deep_translator import GoogleTranslator
from dotenv import load_dotenv
import re  # Vẫn giữ reเผื่อ có trường hợp cần thiết, dù TheMealDB ít HTML hơn

# Cấu hình encoding cho Windows
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

def format_time(seconds):
    """Chuyển đổi số giây thành định dạng thời gian dễ đọc"""
    hours = seconds // 3600
    minutes = (seconds % 3600) // 60
    seconds = seconds % 60
    if hours > 0:
        return f"{int(hours)} giờ {int(minutes)} phút {int(seconds)} giây"
    elif minutes > 0:
        return f"{int(minutes)} phút {int(seconds)} giây"
    else:
        return f"{int(seconds)} giây"

def print_time_info(start_time, end_time, message=""):
    """In thông tin thời gian với định dạng đẹp"""
    duration = end_time - start_time
    print("\n" + "="*60)
    print("THÔNG TIN THỜI GIAN CHẠY SCRIPT")
    print("="*60)
    print(f"⏱️  Bắt đầu: {datetime.fromtimestamp(start_time).strftime('%H:%M:%S %d/%m/%Y')}")
    print(f"⏱️  Kết thúc: {datetime.fromtimestamp(end_time).strftime('%H:%M:%S %d/%m/%Y')}")
    print(f"⏱️  Tổng thời gian: {format_time(duration)}")
    if message:
        print("\n" + "="*60)
        print("KẾT QUẢ")
        print("="*60)
        print(f"✅ {message}")
    print("="*60 + "\n")

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
translator = GoogleTranslator(source='auto', target='vi')


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
        # Thêm delay để tránh rate limit
        time.sleep(1)
        
        # Sử dụng GoogleTranslator trực tiếp
        translated_text = translator.translate(original_text)
        
        if not translated_text:
            print(f"Cảnh báo: Dịch thuật cho '{original_text}' trả về kết quả rỗng. Sử dụng văn bản gốc.")
            return original_text

        translated_text = translated_text.strip()
        print(f"Đã dịch: '{original_text}' -> '{translated_text}'")
        return translated_text
        
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
    start_time = time.time()
    print(f"\n🔄 Bắt đầu lấy {num_recipes} công thức nấu ăn...")
    
    conn = get_db_connection()
    if not conn:
        print("❌ Không thể kết nối đến database!")
        return

    cursor = conn.cursor()
    meals_to_process = []  # Danh sách các đối tượng 'meal' từ API để xử lý

    if query:
        search_url = f"{THEMEALDB_BASE_URL}/search.php"
        params = {'s': query}
        print(f"Đang tìm kiếm công thức với từ khóa '{query}' trên TheMealDB...")
        try:
            response = requests.get(search_url, params=params)
            response.raise_for_status()
            data = response.json()
            if data and data.get('meals'):  # API trả về {'meals': null} nếu không tìm thấy
                meals_to_process = data['meals'][:num_recipes]  # Lấy tối đa num_recipes kết quả
                print(f"Tìm thấy {len(data['meals'])} công thức, sẽ xử lý tối đa {len(meals_to_process)} công thức.")
            else:
                print(f"Không tìm thấy công thức nào với từ khóa '{query}'.")
        except requests.exceptions.RequestException as e:
            print(f"Lỗi khi gọi API tìm kiếm TheMealDB: {e}")
        except ValueError as e:  # Lỗi parse JSON
            print(f"Lỗi khi phân tích JSON từ API tìm kiếm TheMealDB: {e}")
            if 'response' in locals() and response: print(f"Nội dung phản hồi: {response.text}")
    else:  # Không có query, lấy ngẫu nhiên
        print(f"Đang lấy {num_recipes} công thức ngẫu nhiên từ TheMealDB...")
        random_url = f"{THEMEALDB_BASE_URL}/random.php"
        for i in range(num_recipes):
            try:
                # Thêm độ trễ nhỏ giữa các lần gọi API ngẫu nhiên
                time.sleep(0.5)
                response = requests.get(random_url)
                response.raise_for_status()
                data = response.json()
                if data and data.get('meals'):
                    meals_to_process.append(data['meals'][0])
                    print(f"Đã lấy công thức ngẫu nhiên thứ {i+1}/{num_recipes}")
                else:
                    print(f"Không thể lấy công thức ngẫu nhiên thứ {i+1}")
            except requests.exceptions.RequestException as e:
                print(f"Lỗi khi gọi API ngẫu nhiên TheMealDB: {e}")
            except ValueError as e:
                print(f"Lỗi khi phân tích JSON từ API ngẫu nhiên TheMealDB: {e}")
                if 'response' in locals() and response: print(f"Nội dung phản hồi: {response.text}")

    # Xử lý và lưu từng công thức
    successful_recipes = 0
    for meal in meals_to_process:
        try:
            print(f"\n🔄 Đang xử lý công thức: {meal.get('strMeal', '')}")
            
            # Dịch các trường văn bản trước
            print("🔄 Đang dịch thông tin công thức...")
            name_vi = translate_text(meal.get('strMeal', ''))
            category_vi = translate_text(meal.get('strCategory', ''))
            area_vi = translate_text(meal.get('strArea', ''))
            instructions_vi = translate_text(meal.get('strInstructions', ''))
            
            print(f"✅ Đã dịch các trường văn bản:")
            print(f"   - Tên: {name_vi}")
            print(f"   - Loại: {category_vi}")
            print(f"   - Vùng: {area_vi}")
            
            # Xử lý nguyên liệu
            ingredients = []
            print("\n🔄 Đang xử lý nguyên liệu...")
            for i in range(1, 21):  # TheMealDB có tối đa 20 nguyên liệu
                ingredient = meal.get(f'strIngredient{i}')
                measure = meal.get(f'strMeasure{i}')
                if ingredient and ingredient.strip():
                    # Dịch tên nguyên liệu
                    ingredient_vi = translate_text(ingredient)
                    # Dịch định lượng nếu có
                    measure_vi = translate_text(measure) if measure else None
                    
                    print(f"   - Nguyên liệu: {ingredient_vi} ({measure_vi})")
                    
                    # Kiểm tra xem nguyên liệu đã tồn tại chưa
                    cursor.execute("SELECT id FROM ingredients WHERE name = %s", (ingredient_vi,))
                    result = cursor.fetchone()
                    
                    if result:
                        ingredient_id = result[0]
                        print(f"     ✓ Đã tồn tại với ID: {ingredient_id}")
                    else:
                        # Thêm nguyên liệu mới
                        cursor.execute(
                            "INSERT INTO ingredients (name, icon) VALUES (%s, %s)",
                            (ingredient_vi, None)
                        )
                        ingredient_id = cursor.lastrowid
                        print(f"     ✓ Đã thêm mới với ID: {ingredient_id}")
                    
                    ingredients.append((ingredient_id, measure_vi))

            print("\n🔄 Đang lưu công thức vào database...")
            # Lưu công thức
            cursor.execute("""
                INSERT INTO recipes (name, description, image, category, area, instructions)
                VALUES (%s, %s, %s, %s, %s, %s)
            """, (
                name_vi,
                f"Công thức {category_vi} từ {area_vi}",
                meal.get('strMealThumb'),
                category_vi,
                area_vi,
                instructions_vi
            ))
            recipe_id = cursor.lastrowid
            print(f"✅ Đã lưu công thức với ID: {recipe_id}")

            # Lưu liên kết nguyên liệu
            print("🔄 Đang lưu liên kết nguyên liệu...")
            for ingredient_id, measure in ingredients:
                cursor.execute("""
                    INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity)
                    VALUES (%s, %s, %s)
                """, (recipe_id, ingredient_id, measure))
                print(f"   ✓ Đã lưu liên kết với nguyên liệu ID: {ingredient_id}")

            successful_recipes += 1
            print(f"✅ Đã hoàn thành xử lý công thức: {name_vi}")

        except mysql.connector.Error as err:
            print(f"❌ Lỗi database khi xử lý công thức: {err}")
        except Exception as e:
            print(f"❌ Lỗi không xác định khi xử lý công thức: {e}")

    conn.commit()
    cursor.close()
    conn.close()

    end_time = time.time()
    message = f"Đã xử lý và lưu thành công {successful_recipes} công thức từ TheMealDB"
    print_time_info(start_time, end_time, message)
    return successful_recipes


def main():
    # Lấy số lượng công thức từ tham số dòng lệnh
    num_recipes = 10  # Giá trị mặc định
    if len(sys.argv) > 1:
        try:
            num_recipes = int(sys.argv[1])
        except ValueError:
            print("Số lượng công thức không hợp lệ, sử dụng giá trị mặc định: 10")

    start_time = time.time()
    print(f"\n🚀 Bắt đầu chạy script...")
    print(f"📅 Thời gian bắt đầu: {datetime.fromtimestamp(start_time).strftime('%H:%M:%S %d/%m/%Y')}")

    try:
        fetch_and_store_recipes_themealdb(num_recipes)
    except Exception as e:
        print(f"❌ Lỗi không mong muốn: {e}")
    finally:
        end_time = time.time()
        print_time_info(start_time, end_time)


# --- Chạy Script ---
if __name__ == "__main__":
    main()