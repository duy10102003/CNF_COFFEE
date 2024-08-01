package com.cnf.controller.admin;

import com.cnf.entity.Product;
import com.cnf.helper.ImportHelper;
import com.cnf.services.CategoryService;
import com.cnf.services.ImportDataService;
import com.cnf.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/product")
public class Admin_ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ImportDataService fileService;
    @GetMapping("")
    public String index(Model model){
        if(model.containsAttribute("message")){
            model.addAttribute("message", model.getAttribute("message"));
        }
        return findPaginated(1, model);
    }
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable (value = "pageNo") int pageNo, Model model) {
        int pageSize = 5;
        Page<Product> page = productService.findPaginated(pageNo, pageSize);
        List<Product> listProduct = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("products", listProduct);
        return "admin/product/index";
    }
    @GetMapping("/add")
    public String addNew(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product/add";
    }
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("product")Product product,
                      BindingResult bindingResult, Model model,
                      @RequestParam("image") MultipartFile multipartFile,
                      RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "_error",
                        error.getDefaultMessage());
            }
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product/add";
        }
        productService.addProduct(product,multipartFile);
        redirectAttributes.addFlashAttribute("message", "Save successfully!");
        return "redirect:/admin/product";
    }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model){
        Product editProduct = null;
        for(Product product: productService.getAllProducts()){
            if(product.getId() == id){
                editProduct = product;
            }
        }
        if(editProduct != null){
            model.addAttribute("product", editProduct);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product/edit";
        }else {
            return "not-found";
        }
    }

    @PostMapping("edit")
    public String edit(@Valid @ModelAttribute("product") Product updateProduct,
                       BindingResult bindingResult, Model model,
                       @RequestParam("image") MultipartFile multipartFile,
                       RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "_error",
                        error.getDefaultMessage());
            }
            model.addAttribute("product",updateProduct);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product/edit";
        }
        productService.updateProduct(updateProduct, multipartFile);
        redirectAttributes.addFlashAttribute("message", "Save successfully!");
        return "redirect:/admin/product";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String message = "";

        if (ImportHelper.hasExcelFormat(file)) {
            try {
                fileService.save(file);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                redirectAttributes.addFlashAttribute("message",message);
                return "redirect:/admin/product";
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                redirectAttributes.addFlashAttribute("message",message);
                return "redirect:/admin/product";
            }
        }
        message = "Please upload an excel file!";
        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/admin/product";
    }

    @GetMapping("/download")
    public String getFile() {
        String filename = "tutorials.xlsx";
        InputStreamResource file = new InputStreamResource(fileService.load());

//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
//                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
//                .body(file);
        return "redirect:/admin/product";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes ) {
        try {
            productService.deleteProductById(id);
            redirectAttributes.addFlashAttribute("message","Delete successfully!");
            return "redirect:/admin/product" ;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message","Failed to delete product");
            return "redirect:/admin/product" ;

        }
    }

}
