package com.sidoca.controllers;

// import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public abstract class BaseController {

    // Setara dengan loadView($viewName, $data)
    protected ModelAndView loadView(String viewName, java.util.Map<String, Object> data) {
        ModelAndView mav = new ModelAndView(viewName);
        if (data != null) {
            mav.addAllObjects(data);
        }
        return mav;
    }

    // Setara dengan loadModel($modelName)
    // Di Spring Boot, model biasanya berupa service/repository otomatis dengan @Autowired
}

