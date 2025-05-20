import mysql.connector
import os
from dotenv import load_dotenv
import unicodedata  # Thư viện để chuẩn hóa chuỗi tiếng Việt (không bắt buộc dùng)

# Load environment variables from .env file
load_dotenv()

# --- Database Configuration ---
DB_HOST = os.getenv('DB_HOST', 'localhost')
DB_USER = os.getenv('DB_USER')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_NAME = os.getenv('DB_NAME')

# --- BẢN ĐỒ ÁNH XẠ TÊN NGUYÊN LIỆU -> ICON ---
# Phiên bản cải tiến, sử dụng Font Awesome 5/6 Free
ICON_MAPPING = {
    # ----- THỊT (MEATS) -----
    "thịt bò": "fas fa-drumstick-bite",
    "thịt bò xay": "fas fa-drumstick-bite",  # Ground beef
    "bít tết": "fas fa-drumstick-bite",  # Steak
    "chuck nướng": "fas fa-drumstick-bite",  # Chuck roast
    "thịt bò bị dồn dập": "fas fa-drumstick-bite",
    "bít tết sườn": "fas fa-drumstick-bite",  # Rib steak
    "bít tết sirloin": "fas fa-drumstick-bite",  # Sirloin steak
    "bít tết bít tết": "fas fa-drumstick-bite",  # Skirt steak / Filet steak
    "thịt hầm": "fas fa-drumstick-bite",  # Stew meat
    "thịt bò ngắn": "fas fa-drumstick-bite",  # Beef short ribs

    "thịt heo": "fas fa-bacon",  # Pork
    "thịt lợn": "fas fa-bacon",  # Pork (synonym)
    "giăm bông": "fas fa-bacon",  # Ham
    "thịt lợn vai nướng": "fas fa-bacon",  # Pork shoulder roast

    "gà": "fas fa-drumstick-bite",  # Chicken
    "ức gà": "fas fa-drumstick-bite",  # Chicken breast
    "đùi gà": "fas fa-drumstick-bite",  # Chicken thigh
    "cánh gà": "fas fa-drumstick-bite",  # Chicken wings
    "gà một nửa": "fas fa-drumstick-bite",  # Half chicken
    "thịt gà": "fas fa-drumstick-bite",  # Chicken meat

    "thổ nhĩ kỳ": "fas fa-feather",  # Turkey
    "gà tây mặt đất": "fas fa-feather",  # Ground turkey

    "xúc xích": "fas fa-hotdog",  # Sausage
    "một xúc xích bán hun khói": "fas fa-hotdog",
    "liên kết chorizo": "fas fa-hotdog",  # Chorizo link

    # ----- HẢI SẢN (SEAFOOD) -----
    "cá": "fas fa-fish",
    "philê cá": "fas fa-fish",  # Fish fillet
    "filet of rainbow trout": "fas fa-fish",
    "philê cá tuyết": "fas fa-fish",  # Cod fillet
    "fillet cá hồi": "fas fa-fish",  # Salmon fillet
    "viên philon cá hồi sockeye": "fas fa-fish",
    "philê cá trên da abt 8": "fas fa-fish",
    "philê cá nhẹ": "fas fa-fish",
    "vẹt cá": "fas fa-fish",  # Parrot fish
    "cá ngừ": "fas fa-fish",  # Tuna
    "cá hồi": "fas fa-fish",  # Salmon

    "tôm": "fas fa-shrimp",  # Shrimp (FA6)
    "con tôm": "fas fa-shrimp",  # Shrimp
    "tôm hùm": "fas fa-shrimp",  # Lobster (using shrimp icon as placeholder)
    "tôm hùm maine": "fas fa-shrimp",

    "sò": "fas fa-compact-disc",  # Scallops (generic shell shape)
    "nghêu": "fas fa-compact-disc",  # Clam
    "hàu": "fas fa-compact-disc",  # Oyster

    # ----- TRỨNG, SỮA, PHÔ MAI (EGGS, DAIRY, CHEESE) -----
    "trứng": "fas fa-egg",
    "lòng đỏ": "fas fa-egg",  # Egg yolk
    "lòng trắng": "fas fa-egg",  # Egg white (eggwhite)
    "chất thay thế trứng lỏng": "fas fa-egg",  # Liquid egg substitute

    "sữa": "fas fa-cow",  # Milk
    "sữa không béo": "fas fa-cow",  # Skim milk
    "sữa hạnh nhân": "fas fa-mug-hot",  # Almond milk
    "sữa đậu nành": "fas fa-mug-hot",  # Soy milk
    # "nước cốt dừa" (dùng uống) -> "fas fa-mug-hot"
    # "nước cốt dừa" (nấu ăn) -> "fas fa-coconut" (xem mục quả)

    "kem": "fas fa-ice-cream",  # Cream (general) / Whipped cream
    "kem nặng": "fas fa-ice-cream",  # Heavy cream
    "kem đánh bông": "fas fa-ice-cream",
    "kem roi": "fas fa-ice-cream",
    "kem súp kem": "fas fa-ice-cream",  # Cream soup base?

    "phô mai": "fas fa-cheese",  # Cheese (general)
    "phô mai parmesan": "fas fa-cheese",
    "parmesan": "fas fa-cheese",
    "phô mai cheddar": "fas fa-cheese",
    "phô mai cheddar sắc nét": "fas fa-cheese",
    "phô mai mozzarella": "fas fa-cheese",
    "phô mai dê": "fas fa-cheese",  # Goat cheese
    "hỗn hợp phô mai mexico": "fas fa-cheese",  # Mexican cheese blend
    "hạt tiêu jack phô mai": "fas fa-cheese",  # Pepper Jack cheese
    "kem phô mai": "fas fa-cheese",  # Cream cheese
    "phô mai nhẹ": "fas fa-cheese",  # Light cheese
    "pkt kem phô mai": "fas fa-cheese",  # Packet cream cheese
    "phô mai kem philadelphia": "fas fa-cheese",
    "phô mai feta": "fas fa-cheese",

    "bơ": "fas fa-butter",  # Butter (dairy)
    "bơ thực vật": "fas fa-butter",  # Margarine
    "bơ hạnh nhân tự nhiên": "fas fa-butter",  # Almond butter (using butter icon)

    "sữa chua": "fas fa-stroopwafel",  # Yogurt (generic tub icon)
    "da ua": "fas fa-stroopwafel",  # Yogurt
    "sữa chua hy lạp": "fas fa-stroopwafel",  # Greek yogurt
    "lowfat yogurt": "fas fa-stroopwafel",

    # ----- RAU CỦ (VEGETABLES) -----
    "cà chua": "fas fa-apple-whole",  # Tomato
    "cà chua đóng hộp": "fas fa-jar",  # Canned tomato
    "nước sốt cà chua": "fas fa-bottle-droplet",  # Tomato sauce
    "bột cà chua": "fas fa-mortar-pestle",  # Tomato paste
    "cà chua anh đào": "fas fa-apple-whole",  # Cherry tomato
    "cà chua roma": "fas fa-apple-whole",  # Roma tomato
    "cà chua phơi nắng": "fas fa-apple-whole",  # Sun-dried tomato
    "cà chua mận": "fas fa-apple-whole",  # Plum tomato
    "cà chua em bé punnet": "fas fa-apple-whole",
    "cà chua anh đào cầu vồng": "fas fa-apple-whole",
    "nước ép cà chua": "fas fa-glass-water",  # Tomato juice
    "cà chua sundried": "fas fa-apple-whole",  # Sundried tomato

    "hành tây": "fas fa-seedling",  # Onion (fa-onion is Pro)
    "củ hành": "fas fa-seedling",  # Onion
    "hành": "fas fa-seedling",  # Onion (generic)
    "hành tây đỏ": "fas fa-seedling",  # Red onion
    "hành lá": "fas fa-leaf",  # Green onion / Scallions
    "hành lá mùa xuân": "fas fa-leaf",  # Spring onions
    "tops hành lá bổ sung": "fas fa-leaf",

    "tỏi": "fas fa-seedling",  # Garlic (fa-garlic is Pro)
    "tỏi hạt": "fas fa-seedling",
    "tép tỏi": "fas fa-seedling",  # Garlic cloves
    "tỏi đinh hương": "fas fa-seedling",
    "tỏi rang": "fas fa-seedling",  # Roasted garlic
    "bột tỏi": "fas fa-mortar-pestle",  # Garlic powder

    "ớt": "fas fa-pepper-hot",  # Chili pepper
    "ớt chuông": "fas fa-pepper-hot",  # Bell pepper
    "chuông tiêu": "fas fa-pepper-hot",
    "chuông ớt": "fas fa-pepper-hot",
    "tiêu cam": "fas fa-pepper-hot",  # Orange pepper
    "jalapeño": "fas fa-pepper-hot",
    "jalapeno": "fas fa-pepper-hot",
    "hạt tiêu cuba": "fas fa-pepper-hot",  # Cubanelle pepper
    "chipotle chiles": "fas fa-pepper-hot",
    "chili flakes": "fas fa-pepper-hot",
    "rắc hạt tiêu cayenne": "fas fa-pepper-hot",  # Cayenne pepper sprinkle
    "cayenne": "fas fa-pepper-hot",
    "hạt tiêu bonnet scotch": "fas fa-pepper-hot",  # Scotch Bonnet
    "sriracha": "fas fa-pepper-hot",
    "tabasco": "fas fa-pepper-hot",
    "nước sốt habanero và chile": "fas fa-pepper-hot",
    "picante của bạn": "fas fa-pepper-hot",  # Picante sauce
    "tùy chọn: nước sốt nóng": "fas fa-pepper-hot",  # Hot sauce
    "một sriracha mực": "fas fa-pepper-hot",  # Squid ink sriracha (using hot pepper icon)
    "chipotle nước sốt nóng": "fas fa-pepper-hot",
    "chili pepper": "fas fa-pepper-hot",

    "cà rốt": "fas fa-carrot",
    "trên cà rốt": "fas fa-carrot",  # Shredded carrots?

    "khoai tây": "fas fa-carrot",  # Potato (generic root vegetable)
    "khoai tây ngón tay": "fas fa-carrot",  # Fingerling potato
    "khoai tây vàng yukon": "fas fa-carrot",  # Yukon Gold potato
    "khoai tây mới": "fas fa-carrot",  # New potato
    "khoai tây ngọt": "fas fa-carrot",  # Sweet potato

    "nấm": "fas fa-seedling",  # Mushroom (fa-mushroom is Pro)
    "nấm porcini": "fas fa-seedling",
    "nấm tôm hùm": "fas fa-seedling",  # Lobster mushroom

    "đậu phụ": "fas fa-cube",  # Tofu
    "khối lite đậu phụ": "fas fa-cube",  # Lite tofu block

    "bắp cải": "fas fa-leaf",  # Cabbage
    "rau bina": "fas fa-leaf",  # Spinach
    "rau chân vịt": "fas fa-leaf",
    "rau bina bé": "fas fa-leaf",  # Baby spinach
    "rau bina kem": "fas fa-leaf",  # Creamed spinach?
    "lá rau bina": "fas fa-leaf",
    "cải xoăn": "fas fa-leaf",  # Kale
    "kale xoăn": "fas fa-leaf",
    "khủng long kala": "fas fa-leaf",  # Dinosaur Kale
    "kale baby": "fas fa-leaf",
    "lá cải xoăn": "fas fa-leaf",
    "thụy sĩ lá lá": "fas fa-leaf",  # Swiss Chard
    "chard thụy sĩ": "fas fa-leaf",
    "xà lách": "fas fa-leaf",  # Lettuce
    "xà lách boston": "fas fa-leaf",  # Boston lettuce
    "em bé arugula": "fas fa-leaf",  # Baby arugula
    "lá tên lửa": "fas fa-leaf",  # Rocket leaves (arugula)
    "rau xanh": "fas fa-leaf",  # Mixed greens
    "rau": "fas fa-leaf",  # Greens general

    "bông cải xanh": "fas fa-seedling",  # Broccoli
    "hoa bông cải xanh": "fas fa-seedling",  # Broccoli florets
    "broccolini": "fas fa-seedling",
    "súp lơ": "fas fa-seedling",  # Cauliflower
    "súp lơ màu cam": "fas fa-seedling",
    "một súp lơ": "fas fa-seedling",

    "cần tây": "fas fa-seedling",  # Celery
    "celery stalks": "fas fa-seedling",

    "đậu": "fas fa-seedling",  # Beans (general)
    "đậu lăng": "fas fa-seedling",  # Lentils
    "đậu hà lan": "fas fa-seedling",  # Peas
    "hà lan mắt đen": "fas fa-seedling",  # Black-eyed peas
    "hà lan chụp": "fas fa-seedling",  # Snap peas
    "peas mắt": "fas fa-seedling",
    "đậu xanh": "fas fa-seedling",  # Green beans
    "đậu xanh khô": "fas fa-seedling",  # Dried green beans
    "đậu đen": "fas fa-seedling",  # Black beans
    "đậu thận": "fas fa-seedling",  # Kidney beans
    "đậu garbanzo *1": "fas fa-seedling",  # Chickpeas
    "hải quân": "fas fa-seedling",  # Navy beans
    "đậu bơ": "fas fa-seedling",  # Butter beans
    "đậu sáp": "fas fa-seedling",  # Wax beans

    "dưa chuột": "fas fa-lemon",  # Cucumber (using lemon as shape proxy)
    "quả dưa chuột": "fas fa-lemon",

    "mầm brussels": "fas fa-seedling",  # Brussels sprouts
    "em bé brussel sprouts": "fas fa-seedling",

    "cà tím": "fas fa-eggplant",  # Eggplant (FA6)
    "măng tây": "fas fa-seedling",  # Asparagus
    "atisô": "fas fa-seedling",  # Artichoke

    "zucchini": "fas fa-seedling",  # Zucchini
    "bí đỏ": "fas fa-pumpkin",  # Pumpkin (general)
    "bí ngô": "fas fa-pumpkin",
    "butternut squash": "fas fa-pumpkin",  # (FA6)
    "bóng đèn thì là": "fas fa-seedling",  # Fennel bulb

    "ngô": "fas fa-corn",  # Corn (FA6)
    "ngô kernel": "fas fa-corn",  # Corn kernel
    "bắp": "fas fa-corn",  # Corn (synonym)

    "củ cải": "fas fa-carrot",  # Radish (using carrot as root veg proxy)
    "parsnip": "fas fa-carrot",  # Parsnip

    "rau dền": "fas fa-leaf",  # Amaranth leaves
    "garden cress": "fas fa-leaf",
    "micro greens": "fas fa-leaf",

    # ----- GIA VỊ & THẢO MỘC (SPICES & HERBS) -----
    "gia vị": "fas fa-mortar-pestle",  # Seasoning (general)
    "muối": "fas fa-cube",  # Salt
    "muối biển": "fas fa-cube",
    "biển-sal": "fas fa-cube",  # Sea salt
    "muối kosher": "fas fa-cube",
    "muối dày dạn": "fas fa-mortar-pestle",  # Seasoned salt
    "muối thảo dược": "fas fa-mortar-pestle",  # Herbal salt
    "muối masala": "fas fa-mortar-pestle",
    "muối cần tây": "fas fa-mortar-pestle",  # Celery salt
    "muối & hạt tiêu": "fas fa-mortar-pestle",  # Salt & Pepper (single icon for mixed)
    "muối và hạt tiêu": "fas fa-mortar-pestle",

    "hạt tiêu": "fas fa-circle",  # Pepper (peppercorns)
    "hạt tiêu đen": "fas fa-circle",  # Black peppercorns
    "tiêu đất": "fas fa-mortar-pestle",  # Ground pepper
    "bữa lông hạt tiêu": "fas fa-mortar-pestle",  # Ground pepper (meal)
    "hạt tiêu nứt": "fas fa-mortar-pestle",  # Cracked pepper
    "hóa gia hạn chanh và hạt tiêu": "fas fa-lemon",  # Lemon pepper seasoning

    "đường": "fas fa-cubes",  # Sugar
    "đường bột": "fas fa-cubes",  # Powdered sugar
    "đường nâu": "fas fa-cubes",  # Brown sugar

    "húng tây": "fas fa-leaf",  # Thyme
    "thyme": "fas fa-leaf",
    "thyme khô": "fas fa-leaf",
    "húng quế": "fas fa-leaf",  # Basil
    "lá húng quế": "fas fa-leaf",
    "lá húng quế tươi": "fas fa-leaf",
    "basil thái": "fas fa-leaf",  # Thai Basil
    "bạn có thể sử dụng húng quế thông thường": "fas fa-leaf",  # (key: "húng quế")

    "kinh giới": "fas fa-leaf",  # Oregano
    "rau oregano": "fas fa-leaf",
    "marjoram": "fas fa-leaf",  # Marjoram (similar to oregano)
    "ngò tây": "fas fa-leaf",  # Parsley
    "rau mùi tây": "fas fa-leaf",
    "rau mùi tây phẳng": "fas fa-leaf",  # Flat parsley
    "lá rau mùi tây": "fas fa-leaf",
    "ngò": "fas fa-leaf",  # Cilantro / Coriander leaves
    "rau mùi": "fas fa-leaf",
    "lá rau mùi": "fas fa-leaf",
    "của rau mùi": "fas fa-leaf",  # (key: "rau mùi")
    "ngò lông tươi": "fas fa-leaf",  # Fresh cilantro
    "rau mùi mặt đất": "fas fa-mortar-pestle",  # Ground coriander

    "hương thảo": "fas fa-leaf",  # Rosemary
    "rosemary": "fas fa-leaf",
    "lá hương thảo": "fas fa-leaf",
    "rosemary và húng tây": "fas fa-leaf",  # (key: "hương thảo" or "húng tây")

    "bạc hà": "fas fa-leaf",  # Mint
    "lá bạc hà": "fas fa-leaf",
    "lá bạc hà tươi": "fas fa-leaf",

    "thì là (lá)": "fas fa-leaf",  # Dill weed (lá)
    "dill weed": "fas fa-leaf",
    "thì là": "fas fa-leaf",  # (can be fennel herb or dill)
    "hạt thì là": "fas fa-mortar-pestle",  # Fennel seeds / Dill seeds
    "bột hạt thì là": "fas fa-mortar-pestle",

    "xô thơm": "fas fa-leaf",  # Sage
    "hiền nhân mặt đất": "fas fa-mortar-pestle",  # Ground Sage

    "lá nguyệt quế": "fas fa-leaf",  # Bay leaf
    "hẹ": "fas fa-leaf",  # Chives
    "rau mùi tây và/hoặc hẹ": "fas fa-leaf",
    "herbs de provence": "fas fa-leaf",  # Mixed herbs

    "gừng": "fas fa-seedling",  # Ginger (root shape)
    "gừng xay": "fas fa-mortar-pestle",  # Ground ginger
    "bột gừng": "fas fa-mortar-pestle",
    "mảnh gừng centimet": "fas fa-seedling",

    "nghệ": "fas fa-mortar-pestle",  # Turmeric (powder)
    "củ nghệ mặt đất": "fas fa-mortar-pestle",

    "thảo quả": "fas fa-seedling",  # Cardamom (pods)
    "đinh hương": "fas fa-star-of-life",  # Cloves (shape a bit like it)

    "quế": "fas fa-mortar-pestle",  # Cinnamon (powder)
    "quế xay": "fas fa-mortar-pestle",
    "dsh cinnamon": "fas fa-mortar-pestle",  # (key: "quế")
    "không bắt buộc;quế": "fas fa-mortar-pestle",  # (key: "quế")
    "thanh quế": "fas fa-bars",  # Cinnamon sticks

    "nhục đậu khấu": "fas fa-circle",  # Nutmeg (whole)
    "dsh nutmeg": "fas fa-circle",  # (key: "nhục đậu khấu")
    "nutmeg mặt đất": "fas fa-mortar-pestle",  # Ground nutmeg

    "cumin": "fas fa-mortar-pestle",  # Cumin (powder or seed)
    "cumin mặt đất": "fas fa-mortar-pestle",
    "paprika": "fas fa-mortar-pestle",  # Paprika powder
    "bột cà ri": "fas fa-mortar-pestle",  # Curry powder
    "bột chile": "fas fa-mortar-pestle",  # Chili powder (distinct from chili flakes)
    "chili powder": "fas fa-mortar-pestle",

    "hạt cần tây": "fas fa-circle",  # Celery seed
    "hạt rau mùi": "fas fa-circle",  # Coriander seed
    "hạt mù tạt": "fas fa-circle",  # Mustard seed
    "mù tạt mặt đất": "fas fa-mortar-pestle",  # Ground mustard
    "bột mù tạt xay": "fas fa-mortar-pestle",
    "mù tạt dijon": "fas fa-jar",  # Dijon mustard (paste form, using jar)

    "vani": "fas fa-cookie",  # Vanilla (general, bean or flavor)
    "chiết xuất vani": "fas fa-bottle-droplet",  # Vanilla extract
    "vanilla pod": "fas fa-seedling",  # Vanilla bean

    "capers": "fas fa-circle",  # Capers
    "ô liu": "fas fa-circle",  # Olives
    # " оливки": "fas fa-circle", # Russian for olives, remove if not common in your data

    "asafetida": "fas fa-mortar-pestle",  # Asafoetida (hing) powder
    "suya spice": "fas fa-mortar-pestle",  # Suya spice blend

    # ----- DẦU & CHẤT BÉO (OILS & FATS) -----
    "dầu": "fas fa-bottle-droplet",  # Oil (general)
    "dầu ô liu": "fas fa-bottle-droplet",
    "dầu ô liu nguyên chất thêm": "fas fa-bottle-droplet",
    "evoo": "fas fa-bottle-droplet",
    "dầu ô liu ngâm húng quế": "fas fa-bottle-droplet",
    "dầu ô liu để áo": "fas fa-bottle-droplet",
    "dầu mè": "fas fa-bottle-droplet",
    "dầu thực vật": "fas fa-bottle-droplet",
    "dầu cải dầu": "fas fa-bottle-droplet",  # Canola oil
    "dầu nho": "fas fa-bottle-droplet",  # Grapeseed oil
    "dầu dừa": "fas fa-coconut",  # Coconut oil (using coconut icon)
    "ghee": "fas fa-butter",  # Ghee (clarified butter)

    # ----- NGŨ CỐC, BỘT, MÌ, BÁNH (GRAINS, FLOUR, PASTA, BAKED GOODS) -----
    "gạo": "fas fa-bowl-rice",  # Rice (FA6)
    "cơm": "fas fa-bowl-rice",
    "gạo nâu": "fas fa-bowl-rice",  # Brown rice
    "gạo arborio": "fas fa-bowl-rice",  # Arborio rice
    "quinoa": "fas fa-bowl-rice",  # Quinoa (can be like rice)
    "quinoa và gạo nâu hỗn hợp": "fas fa-bowl-rice",
    "couscous": "fas fa-bowl-rice",  # Couscous

    "mì ống": "fas fa-bowl-rice",  # Pasta (general, using rice bowl as generic grain dish)
    "mì": "fas fa-bowl-rice",  # Noodles
    "bướm mì ống": "fas fa-bowl-rice",  # Farfalle pasta
    "mì soba": "fas fa-bowl-rice",  # Soba noodles
    "pappardelle": "fas fa-bowl-rice",
    "macaroni khuỷu tay": "fas fa-bowl-rice",  # Elbow macaroni
    "rubini mì ống": "fas fa-bowl-rice",  # Rubini pasta

    "lúa mạch": "fas fa-wheat-awn",  # Barley (FA6)
    "lúa mạch ngọc trai": "fas fa-wheat-awn",
    "yến mạch": "fas fa-wheat-awn",  # Oats
    "farro": "fas fa-wheat-awn",  # Farro

    "bột mì": "fas fa-wheat-awn",  # Flour
    "tất cả các mục đích bột mì": "fas fa-wheat-awn",  # All-purpose flour
    "selfraising bột": "fas fa-wheat-awn",  # Self-raising flour
    "bột hạnh nhân": "fas fa-wheat-awn",  # Almond flour
    "bánh bột": "fas fa-wheat-awn",  # Cake flour
    "bột ngô": "fas fa-corn",  # Corn flour / Cornstarch (using corn icon)

    "bánh mì": "fas fa-bread-slice",  # Bread
    "bánh mỳ": "fas fa-bread-slice",
    "lớn của bánh mì": "fas fa-bread-slice",  # (key: "bánh mì")
    "bánh mì lúa mì": "fas fa-bread-slice",  # Wheat bread
    "bánh mì vụn": "fas fa-bread-slice",  # Breadcrumbs
    "bánh quy": "fas fa-cookie",  # Biscuit / Cracker
    "bánh ngô": "fas fa-bread-slice",  # Cornbread / Tortilla

    "nấm men": "fas fa-flask",  # Yeast
    "bột nở": "fas fa-flask",  # Baking powder
    "baking soda": "fas fa-flask",  # Baking soda
    "kem của cao răng": "fas fa-flask",  # Cream of tartar

    "puff-passry làm sẵn": "fas fa-layer-group",  # Puff pastry
    "hỗn hợp bánh": "fas fa-birthday-cake",  # Cake mix
    "bánh vani": "fas fa-birthday-cake",  # Vanilla wafers/cake
    "cookie oreo": "fas fa-cookie",
    "oreo": "fas fa-cookie",
    "gói cuộn mùa xuân": "fas fa-scroll",  # Spring roll wrappers

    # ----- ĐỒ UỐNG, NƯỚC SỐT, GIA VỊ LỎNG (BEVERAGES, SAUCES, LIQUID SEASONINGS) -----
    "nước": "fas fa-tint",  # Water
    "nước *2": "fas fa-tint",  # (key: "nước")
    "chất lỏng": "fas fa-tint",  # Liquid (general)
    "nước 4 qts": "fas fa-tint",  # (key: "nước")

    "rượu vang trắng": "fas fa-wine-glass",  # White wine
    "rượu vang đỏ": "fas fa-wine-glass",  # Red wine
    "rượu nấu ăn": "fas fa-wine-glass",  # Cooking wine
    "sherry": "fas fa-wine-glass",  # Sherry
    "bia": "fas fa-beer-mug-empty",  # Beer

    "nước dùng": "fas fa-box",  # Broth / Stock (general)
    "cổ phần": "fas fa-box",  # Stock
    "nước dùng thịt bò": "fas fa-box",
    "nước dùng thịt bò từ 1 khối": "fas fa-box",  # (key: "nước dùng thịt bò")
    "bouillon thịt bò": "fas fa-box",
    "thịt bò bouillon cubes": "fas fa-box",
    "nước dùng gà": "fas fa-box",
    "nước dùng rau": "fas fa-box",
    "nước mồi": "fas fa-box",  # Bouillon water
    "nước và 2 gói gà bouillon": "fas fa-box",  # (key: "nước dùng gà")
    "gà và 2 cốc nước": "fas fa-box",  # (key: "nước dùng gà")

    "nước tương": "fas fa-bottle-droplet",  # Soy sauce
    "tôi là nước sốt": "fas fa-bottle-droplet",  # (Old key for soy sauce)
    "nước mắm": "fas fa-bottle-droplet",  # Fish sauce
    "nước sốt hàu": "fas fa-bottle-droplet",  # Oyster sauce
    "sốt worcestershire": "fas fa-bottle-droplet",  # Worcestershire sauce
    "thư mục nước sốt": "fas fa-bottle-droplet",  # (key: "sốt worcestershire")
    "sốt teriyaki": "fas fa-bottle-droplet",  # Teriyaki sauce
    "nước sốt bbq": "fas fa-bottle-droplet",  # BBQ sauce
    "nước sốt lũ xanh": "fas fa-bottle-droplet",  # Green goddess dressing?

    "mayonnaise": "fas fa-jar",  # Mayonnaise (using jar icon)
    "ketchup": "fas fa-bottle-droplet",  # Ketchup

    "giấm": "fas fa-wine-bottle",  # Vinegar
    "giấm balsamic": "fas fa-wine-bottle",
    "giấm rượu vang đỏ": "fas fa-wine-bottle",
    "giấm rượu vang trắng": "fas fa-wine-bottle",  # White wine vinegar
    "giấm táo": "fas fa-apple-whole",  # Apple cider vinegar (using apple icon)
    "giấm gạo": "fas fa-bowl-rice",  # Rice vinegar (using rice icon)
    "giấm sherry": "fas fa-wine-bottle",

    "mật ong": "fas fa-jar",  # Honey
    "em yêu": "fas fa-jar",  # (key: "mật ong")
    "xi-rô cây phong": "fas fa-jar",  # Maple syrup
    "xi-rô ngô": "fas fa-jar",  # Corn syrup

    "caramel": "fas fa-bottle-droplet",  # Caramel sauce
    "sôcôla (lỏng/sốt)": "fas fa-bottle-droplet",  # Chocolate sauce
    "sôcôla": "fas fa-cookie",  # Chocolate (solid)

    "khói lỏng": "fas fa-smog",  # Liquid smoke
    "chiết xuất hạnh nhân": "fas fa-bottle-droplet",  # Almond extract

    "nước cốt chanh": "fas fa-lemon",  # Lemon juice
    "nước chanh": "fas fa-lemon",
    "nước cốt chanh dây": "fas fa-lemon",  # Lime juice (using lemon icon)
    "nước ép vôi": "fas fa-lemon",
    "nước cam": "fas fa-lemon",  # Orange juice (using lemon icon)
    "nước táo": "fas fa-apple-whole",  # Apple juice

    "chutney": "fas fa-jar",  # Chutney
    "thương dán": "fas fa-jar",  # Tamarind paste
    "miso dán": "fas fa-jar",  # Miso paste
    "tahini": "fas fa-jar",  # Tahini paste

    "dale's drafting": "fas fa-bottle-droplet",  # Dale's seasoning
    "trader joe's spicy peanut vinaigrette": "fas fa-bottle-droplet",
    "nuoc cham": "fas fa-bottle-droplet",  # Vietnamese dipping sauce
    "bragggs amino lỏng": "fas fa-bottle-droplet",  # Bragg's Liquid Aminos

    # ----- QUẢ & HẠT (FRUITS & NUTS) -----
    "trái cây": "fas fa-apple-whole",  # Fruit (general)
    "táo": "fas fa-apple-whole",  # Apple
    "quả táo": "fas fa-apple-whole",
    "lê": "fas fa-apple-whole",  # Pear (fa-pear is Pro)
    "đào": "fas fa-apple-whole",  # Peach
    "mận": "fas fa-apple-whole",  # Plum
    "anh đào": "fas fa-apple-whole",  # Cherry
    "anh đào khô": "fas fa-apple-whole",  # Dried cherries

    "cam": "fas fa-lemon",  # Orange (fa-orange is Pro, using lemon as citrus proxy)
    "quả cam": "fas fa-lemon",
    "quýt": "fas fa-lemon",  # Mandarin/Tangerine
    "bưởi": "fas fa-lemon",  # Grapefruit
    "chanh": "fas fa-lemon",  # Lemon (general, both yellow and green lime)
    "chanh vàng": "fas fa-lemon",  # Lemon (yellow)
    "chanh xanh": "fas fa-lemon",  # Lime
    "vỏ chanh": "fas fa-lemon",  # Lemon zest
    "nêm chanh": "fas fa-lemon",  # Lemon wedge
    "clementine": "fas fa-lemon",

    "chuối": "fas fa-banana",  # Banana (FA6)
    "dâu tây": "fas fa-apple-whole",  # Strawberry (fa-strawberry is Pro, use apple proxy)
    "việt quất": "fas fa-circle",  # Blueberry (small round fruit)
    "mâm xôi": "fas fa-circle",  # Raspberry/Blackberry

    "nho": "fas fa-seedling",  # Grape (fa-grapes is FA6)
    "nho khô": "fas fa-seedling",  # Raisins

    "xoài": "fas fa-lemon",  # Mango (using lemon as tropical fruit proxy)
    "mango": "fas fa-lemon",
    "quả xoài": "fas fa-lemon",
    "dứa": "fas fa-seedling",  # Pineapple (fa-pineapple-slice is Pro)
    "quả dứa": "fas fa-seedling",
    "dứa khối": "fas fa-cubes",  # Pineapple chunks

    "dừa": "fas fa-coconut",  # Coconut (FA6)
    "nước cốt dừa": "fas fa-coconut",  # Coconut milk for cooking (using coconut icon)
    "dừa daconut": "fas fa-coconut",  # Shredded coconut? (key: "dừa vụn")
    "dưa hấu": "fas fa-watermelon-slice",  # Watermelon (FA6)
    "dưa hấu xay nhuyễn": "fas fa-watermelon-slice",
    "dưa lưới": "fas fa-lemon",  # Cantaloupe/Melon (proxy)

    "kiwi": "fas fa-kiwi-fruit",  # Kiwi (FA6)
    "kiwis": "fas fa-kiwi-fruit",
    "bơ (trái)": "fas fa-lemon",  # Avocado (fa-avocado is Pro, using lemon as green fruit proxy)

    "hạt": "fas fa-seedling",  # Nuts/Seeds (general)
    "hạnh nhân": "fas fa-tree",  # Almonds
    "hạnh nhân nướng": "fas fa-tree",
    "quả óc chó": "fas fa-tree",  # Walnuts
    "hạt điều": "fas fa-tree",  # Cashews
    "hạt điều rang": "fas fa-tree",
    "hạt điều ngâm qua đêm trong nước": "fas fa-tree",
    "pecans": "fas fa-tree",  # Pecans
    "hạt dẻ cười": "fas fa-tree",  # Pistachios
    "pistachios không có bóng": "fas fa-tree",
    "hạt hồ trăn": "fas fa-tree",  # Pistachios
    "đậu phộng": "fas fa-tree",  # Peanuts
    "hạt thông": "fas fa-tree",  # Pine nuts

    "hạt mè": "fas fa-circle",  # Sesame seeds
    "hạt bí": "fas fa-circle",  # Pumpkin seeds
    "hạt hướng dương": "fas fa-sun",  # Sunflower seeds (using sun icon)
    "hạt chia": "fas fa-circle",  # Chia seeds
    "hạt lanh": "fas fa-circle",  # Flax seeds

    # ----- KHÁC (MISCELLANEOUS) -----
    "gelatin": "fas fa-puzzle-piece",  # Gelatin
    "knox gelatin": "fas fa-puzzle-piece",
    "màu thực phẩm": "fas fa-palette",  # Food coloring
    "bột trà matcha": "fas fa-leaf",  # Matcha powder
    "phải": "fas fa-jar",  # Relish
    "trang trí": "fas fa-star",  # Garnish
    "toppings bổ sung: bơ": "fas fa-plus-circle",  # Assuming "Bơ" here is generic topping
    "khối gia vị": "fas fa-cube",  # Spice cube/bouillon cube
    "khối cầu": "fas fa-globe",  # "Sphere/globe" - food context? (e.g. melon balls)
}


def normalize_string(text):
    """Chuẩn hóa chuỗi: bỏ dấu, chuyển thường, bỏ khoảng trắng thừa."""
    if text is None:
        return ""
    # Hiện tại không bỏ dấu để khớp với ICON_MAPPING đang có dấu
    # text = unicodedata.normalize('NFKD', text).encode('ascii', 'ignore').decode('utf-8')
    return text.strip().lower()


def update_ingredient_icons():
    """Kết nối DB, đọc nguyên liệu và cập nhật icon dựa trên bản đồ hoặc gán mặc định."""
    conn = None
    cursor = None
    updated_count = 0
    default_assigned_count = 0
    skipped_count = 0
    processed_count = 0

    # --- ICON MẶC ĐỊNH ---
    DEFAULT_ICON_CLASS = "fas fa-utensils"  # Đổi icon mặc định thành chung chung hơn
    # --- ------------- ---

    if not DB_USER or not DB_PASSWORD or not DB_NAME:
        print("Lỗi: Vui lòng cấu hình đầy đủ thông tin database trong file .env")
        return

    try:
        conn = mysql.connector.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME
        )
        cursor = conn.cursor()
        print("Kết nối Database thành công!")

        fetch_sql = "SELECT id, name, icon FROM ingredients"
        cursor.execute(fetch_sql)
        ingredients_to_update = cursor.fetchall()
        total_ingredients = len(ingredients_to_update)
        print(f"Tìm thấy {total_ingredients} nguyên liệu để kiểm tra/cập nhật icon.")

        update_sql = "UPDATE ingredients SET icon = %s WHERE id = %s"

        for ingredient_id, ingredient_name, current_icon in ingredients_to_update:
            processed_count += 1
            icon_to_set = None
            is_default = False  # Reset flag for each ingredient

            if not ingredient_name:
                print(f"({processed_count}/{total_ingredients}) Bỏ qua ID: {ingredient_id} vì tên rỗng.")
                skipped_count += 1
                continue

            normalized_name = normalize_string(ingredient_name)
            icon_to_set = ICON_MAPPING.get(normalized_name)

            if not icon_to_set:
                # Thử tìm kiếm một phần nếu tên quá dài hoặc chứa các từ khóa chung
                # Ví dụ: nếu "thịt bò xay nhuyễn loại 1" -> tìm "thịt bò xay"
                # Đây là logic nâng cao hơn, hiện tại chỉ dùng map trực tiếp
                # print(f"({processed_count}/{total_ingredients}) Không tìm thấy map trực tiếp cho '{ingredient_name}'.")

                # Logic tìm kiếm mở rộng (ví dụ đơn giản: tìm từ khóa cuối)
                # Tạm thời không dùng logic này để giữ sự đơn giản, tập trung vào ICON_MAPPING
                # for key_map, icon_val in ICON_MAPPING.items():
                #     if key_map in normalized_name: # Nếu một từ khóa trong map xuất hiện trong tên
                #         icon_to_set = icon_val
                #         print(f"    -> Tìm thấy map dựa trên từ khóa con '{key_map}' cho '{ingredient_name}': '{icon_to_set}'")
                #         break

                if not icon_to_set:  # Vẫn không tìm thấy sau khi (có thể) tìm mở rộng
                    print(
                        f"({processed_count}/{total_ingredients}) Không tìm thấy map cho '{ingredient_name}'. Sẽ gán icon mặc định: '{DEFAULT_ICON_CLASS}'")
                    icon_to_set = DEFAULT_ICON_CLASS
                    is_default = True
            else:
                print(
                    f"({processed_count}/{total_ingredients}) Tìm thấy map trực tiếp cho '{ingredient_name}': '{icon_to_set}'")

            if icon_to_set and (current_icon is None or current_icon != icon_to_set):
                try:
                    cursor.execute(update_sql, (icon_to_set, ingredient_id))
                    if is_default:
                        default_assigned_count += 1
                        # print(f"    -> Đã gán icon MẶC ĐỊNH cho ID: {ingredient_id}") # Log này có thể nhiều quá
                    else:
                        updated_count += 1
                        # print(f"    -> Đã cập nhật icon TỪ MAP cho ID: {ingredient_id}") # Log này có thể nhiều quá
                except mysql.connector.Error as err:
                    print(f"    -> Lỗi khi cập nhật ID: {ingredient_id} ('{ingredient_name}'): {err}")
                    skipped_count += 1
            elif icon_to_set and current_icon == icon_to_set:
                # print(f"    -> Bỏ qua ID: {ingredient_id} - Icon '{current_icon}' đã đúng.") # Log này có thể nhiều quá
                skipped_count += 1
            else:
                print(
                    f"({processed_count}/{total_ingredients}) Bỏ qua ID: {ingredient_id} - Không có icon nào được gán cho '{ingredient_name}'")
                skipped_count += 1

            if processed_count % 100 == 0:  # Log tiến trình mỗi 100 mục
                print(f"Đã xử lý {processed_count}/{total_ingredients}...")

        total_changes = updated_count + default_assigned_count
        if total_changes > 0:
            conn.commit()
            print(
                f"\nĐã commit {total_changes} thay đổi vào database ({updated_count} từ map, {default_assigned_count} mặc định).")
        else:
            print("\nKhông có thay đổi nào được thực hiện (có thể tất cả icon đã đúng hoặc không có gì trong map).")

        print(
            f"\nHoàn thành! Đã xử lý: {processed_count}, Cập nhật từ map: {updated_count}, Gán mặc định: {default_assigned_count}, Bỏ qua/Lỗi/Đã đúng: {skipped_count}")

    except mysql.connector.Error as err:
        print(f"Lỗi Database tổng thể: {err}")
        if conn and conn.is_connected():  # Kiểm tra conn tồn tại trước khi rollback
            conn.rollback()
    finally:
        if cursor:
            cursor.close()
        if conn and conn.is_connected():
            conn.close()
            print("Đã đóng kết nối Database.")


# --- Chạy Script ---
if __name__ == "__main__":
    update_ingredient_icons()