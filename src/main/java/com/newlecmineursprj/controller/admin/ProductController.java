package com.newlecmineursprj.controller.admin;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.newlecmineursprj.entity.Category;
import com.newlecmineursprj.entity.Product;
import com.newlecmineursprj.entity.ProductView;
import com.newlecmineursprj.service.CategoryService;
import com.newlecmineursprj.service.ProductSubImgService;
import com.newlecmineursprj.service.ProductService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("admin/products")
@Controller("adminProductController")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private static final String PRODUCTS_VIEW = "/admin/products";
    private static final String REDIRECT = "redirect:";

    private final ProductService service;
    private final CategoryService categoryService;
    private final ProductSubImgService productSubImgService;

    @GetMapping
    public String list(@RequestParam(required = false) String searchMethod
            , @RequestParam(defaultValue = "") String searchKeyword
            , Model model) {
        List<ProductView> list = service.getList(searchMethod, searchKeyword.trim());
        model.addAttribute("list", list);
        return PRODUCTS_VIEW + "/list";
    }

    @GetMapping("/reg")
    public String regForm(Model model) {
        List<Category> categories = categoryService.getList();
        model.addAttribute("categories", categories);
        return PRODUCTS_VIEW + "/reg";
    }

    @PostMapping
    public String reg(MultipartFile img,
                      Product product,
                      Long categoryId,
                      HttpServletRequest req,
                      MultipartFile[] paths) throws FileUploadException {

        String fileUploadResult = setFileUpload(img, req);

        for (MultipartFile path : paths) {
            setFileUpload(path, req);
        }

        product.setCategoryId(categoryId);
        product.setImgPath(fileUploadResult);
        service.reg(product);
        productSubImgService.regAll(paths, product.getId());
        return REDIRECT + PRODUCTS_VIEW;
    }

    private String setFileUpload(MultipartFile img, HttpServletRequest req) {
        String path = "/image/products";
        String realPath = req.getServletContext().getRealPath(path);
        return service.saveProductImg(img, realPath);
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ProductView product = service.getById(id);
        model.addAttribute("product", product);
        return PRODUCTS_VIEW + "/detail";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam List<Long> deleteId) {
        service.deleteAllById(deleteId);
        return REDIRECT + PRODUCTS_VIEW;
    }

    @PutMapping
    public String edit(@RequestBody Product product) {
        service.edit(product);
        return "success";
    }

}
