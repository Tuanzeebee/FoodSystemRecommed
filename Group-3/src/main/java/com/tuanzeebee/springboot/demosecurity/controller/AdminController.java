package com.tuanzeebee.springboot.demosecurity.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tuanzeebee.springboot.demosecurity.dao.IngredientDTO;
import com.tuanzeebee.springboot.demosecurity.dao.RecipeDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Ingredient;
import com.tuanzeebee.springboot.demosecurity.entity.Role;
import com.tuanzeebee.springboot.demosecurity.entity.User;
import com.tuanzeebee.springboot.demosecurity.repository.RoleRepository;
import com.tuanzeebee.springboot.demosecurity.service.IngredientService;
import com.tuanzeebee.springboot.demosecurity.service.RecipeService;
import com.tuanzeebee.springboot.demosecurity.service.UserService;
import com.tuanzeebee.springboot.demosecurity.service.PostService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final PostService postService;
    
    @Autowired
    public AdminController(UserService userService, 
                          RoleRepository roleRepository, 
                          IngredientService ingredientService,
                          RecipeService recipeService,
                          PostService postService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
        this.postService = postService;
    }
    
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, Authentication authentication) {
        // Tổng số lượng
        long totalUsers = userService.countUsers();
        long totalRecipes = recipeService.countRecipes();
        long totalIngredients = ingredientService.countIngredients();
        long totalComments = postService.countPosts();

        // Lấy thông tin user đang đăng nhập
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        model.addAttribute("user", currentUser);

        // Thêm dữ liệu vào model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalRecipes", totalRecipes);
        model.addAttribute("totalIngredients", totalIngredients);
        model.addAttribute("totalComments", totalComments);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String userManagement(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("newUser", new User());
        return "admin/users";
    }
    
    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User user, @RequestParam List<Long> roleIds, RedirectAttributes redirectAttributes) {
        try {
            Set<Role> roles = roleIds.stream()
                    .map(roleId -> roleRepository.findById(roleId).orElseThrow())
                    .collect(Collectors.toSet());
            user.setRoles(roles);
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Thêm người dùng thành công!"));
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", e.getMessage()));
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, @RequestParam List<Long> roleIds, RedirectAttributes redirectAttributes) {
        try {
            Set<Role> roles = roleIds.stream()
                    .map(roleId -> roleRepository.findById(roleId).orElseThrow())
                    .collect(Collectors.toSet());
            user.setRoles(roles);
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Cập nhật người dùng thành công!"));
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", e.getMessage()));
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Xóa người dùng thành công!"));
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", e.getMessage()));
        }
        return "redirect:/admin/users";
    }
    
    @GetMapping("/ingredients")
    public String showIngredientsPage(Model model) {
        model.addAttribute("ingredients", ingredientService.getAllIngredients());
        return "admin/ingredients";
    }
    
    @PostMapping("/ingredients/add")
    public String addIngredient(Ingredient ingredient, Model model, RedirectAttributes redirectAttributes) {
        try {
            ingredientService.createIngredient(ingredient);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Thêm nguyên liệu thành công!"));
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", e.getMessage()));
        }
        return "redirect:/admin/ingredients";
    }
    
    @PostMapping("/ingredients/update")
    public String updateIngredient(Ingredient ingredient, RedirectAttributes redirectAttributes) {
        try {
            ingredientService.updateIngredient(ingredient.getId(), ingredient);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Cập nhật nguyên liệu thành công!"));
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", e.getMessage()));
        }
        return "redirect:/admin/ingredients";
    }
    
    @PostMapping("/ingredients/delete")
    public String deleteIngredient(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            ingredientService.deleteIngredient(id);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Xóa nguyên liệu thành công!"));
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", e.getMessage()));
        }
        return "redirect:/admin/ingredients";
    }
    @GetMapping("/recipes")
    public String showRecipesPage(Model model) {
        List<RecipeDTO> recipes = recipeService.getAllRecipes();
        
        // Đảm bảo mỗi recipe đều có danh sách ingredient với id
        recipes.forEach(recipe -> {
            if (recipe.getIngredients() == null) {
                recipe.setIngredients(new HashSet<>());
            }
        });
        
        model.addAttribute("recipes", recipes);
        model.addAttribute("ingredients", ingredientService.getAllIngredients());
        return "admin/recipes";
    }
    @PostMapping("/recipes/add")
    public String addRecipe(@RequestParam("name") String name,
                           @RequestParam("description") String description,
                           @RequestParam(value = "ingredients", required = false) List<Long> ingredientIds,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        try {
            RecipeDTO recipeDTO = new RecipeDTO();
            recipeDTO.setName(name);
            recipeDTO.setDescription(description);
            
            // Xử lý file ảnh
            if (!imageFile.isEmpty()) {
                String imagePath = saveImage(imageFile);
                recipeDTO.setImage(imagePath);
            }
            
            // Xử lý ingredients
            if (ingredientIds != null && !ingredientIds.isEmpty()) {
                Set<IngredientDTO> ingredients = ingredientIds.stream()
                        .map(id -> {
                            IngredientDTO dto = new IngredientDTO();
                            dto.setId(id);
                            return dto;
                        })
                        .collect(Collectors.toSet());
                recipeDTO.setIngredients(ingredients);
            } else {
                recipeDTO.setIngredients(new HashSet<>());
            }
            
            recipeService.createRecipe(recipeDTO);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Thêm công thức thành công!"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", e.getMessage()));
        }
        return "redirect:/admin/recipes";
    }
    
    @PostMapping("/recipes/update")
    public String updateRecipe(@RequestParam("id") Long id,
                              @RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam(value = "ingredients", required = false) List<Long> ingredientIds,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              @RequestParam(value = "currentImage", required = false) String currentImage,
                              RedirectAttributes redirectAttributes) {
        try {
            RecipeDTO recipeDTO = recipeService.getRecipeById(id);
            recipeDTO.setName(name);
            recipeDTO.setDescription(description);
            
            // Xử lý file ảnh
            if (!imageFile.isEmpty()) {
                String imagePath = saveImage(imageFile);
                recipeDTO.setImage(imagePath);
            } else if (currentImage != null && !currentImage.isEmpty()) {
                recipeDTO.setImage(currentImage);
            }
            
            // Xử lý ingredients
            if (ingredientIds != null && !ingredientIds.isEmpty()) {
                Set<IngredientDTO> ingredients = ingredientIds.stream()
                        .map(ingredientId -> {
                            IngredientDTO dto = new IngredientDTO();
                            dto.setId(ingredientId);
                            return dto;
                        })
                        .collect(Collectors.toSet());
                recipeDTO.setIngredients(ingredients);
            } else {
                recipeDTO.setIngredients(new HashSet<>());
            }
            
            recipeService.updateRecipe(id, recipeDTO);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Cập nhật công thức thành công!"));
        } catch (RuntimeException | java.io.IOException e) {
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", e.getMessage()));
        }
        return "redirect:/admin/recipes";
    }
    @GetMapping("/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            recipeService.deleteRecipe(id);
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-success", "content", "Xóa công thức thành công!"));
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Có lỗi xảy ra khi xóa công thức";
            redirectAttributes.addFlashAttribute("message", 
                Map.of("type", "alert-danger", "content", errorMessage));
        }
        return "redirect:/admin/recipes";
    }
    
    private String saveImage(MultipartFile file) throws java.io.IOException {
        // Tạo thư mục uploads nếu chưa tồn tại
        String uploadDir = "src/main/resources/static/uploads/recipes";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Tạo tên file duy nhất với timestamp
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString() + fileExtension;
        
        // Lưu file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        
        // Trả về URL đầy đủ để hiển thị
        return "/uploads/recipes/" + fileName;
    }
}