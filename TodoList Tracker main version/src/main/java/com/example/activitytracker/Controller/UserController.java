package com.example.activitytracker.Controller;

import com.example.activitytracker.DTO.TaskDTO;
import com.example.activitytracker.DTO.UserDTO;
import com.example.activitytracker.Model.Task;
import com.example.activitytracker.Model.User;
import com.example.activitytracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
//@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/")
    public  String showRegistrationForm(Model model){
        model.addAttribute("userRegistrationDetails" , new UserDTO());
        return "index";
    }

    @GetMapping(value = "/login")
    public String displayLoginPage(Model model){
        model.addAttribute("userDetails" , new UserDTO());
        return "login";
    }


    @GetMapping("/dashboard")
    public String index(Model model, HttpSession session){
        List<Task> allTasks = userService.allTaskByUserId((Integer) session.getAttribute("id"));
        model.addAttribute("newTaskDetails" , new TaskDTO());
        model.addAttribute("tasks" , allTasks);
        return "dashboard";
    }

    @PostMapping(value = "/loginUser")
    public String loginUser(@RequestParam String email , @RequestParam String password , HttpSession session , Model model){
       String message =  userService.loginUser(email ,  password);

       if(message.equals("Success")){
           User user = userService.getUserByEmail(email);
           session.setAttribute("email" , user.getEmail());
           session.setAttribute("id" , user.getId());
           session.setAttribute("name" , user.getName());
           return "redirect:/dashboard";
       }else{
           model.addAttribute("errorMessage" , message);
           return  "redirect:/login";
       }
    }

    @PostMapping(value = "/userRegistration")
    public String registration(@ModelAttribute UserDTO userDTO){

        User registeredUser = userService.registerUser(userDTO);
        System.out.println(registeredUser);
        if (registeredUser != null){

            return "redirect:/login";
        }else {
            return "redirect:/register";
        }
    }

    @GetMapping(value = "/task/{status}")
    public String taskByStatus(@PathVariable(name = "status") String status , Model model , HttpSession session){
        int user_id = (int) session.getAttribute("id");
        List<Task> listOfTaskByStatus = userService.findAllByUser_idAndStatus(user_id , status);
        model.addAttribute("tasksByStatus" , listOfTaskByStatus);
        return "task-by-status";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable(name = "id") Integer id){
        userService.deleteById(id);
        return "redirect:/dashboard";
    }

    @GetMapping(value = "/editPage/{id}")
    public String showEditPage(@PathVariable(name = "id") Integer id , Model model){
        Task task = userService.getTaskById(id);
        model.addAttribute("singleTask" , task);
        model.addAttribute("taskBody", new TaskDTO());
        return  "editTask";
    }

    @PostMapping(value = "/edit/{id}")
    public String editTask(@PathVariable( name = "id") String id , @ModelAttribute TaskDTO taskDTO){
        int taskId = Integer.parseInt(id);
        userService.updateTitleAndDescription(taskDTO , taskId);
        return "redirect:/dashboard";
    }

    @GetMapping(value = "/addNewTask")
    public String addTask(Model model){
        model.addAttribute("newTask" , new TaskDTO());
        return "addTask";
    }

    @PostMapping(value = "/addTask")
    public String CreateTask(@ModelAttribute TaskDTO taskDTO){
        userService.createTask(taskDTO);
        return "redirect:/dashboard";
    }

    @PostMapping(value = "/changeStatus/{id}")
    public String moveToInProgress(@PathVariable(name = "id")   String id, @RequestParam String status ){
        int taskId = Integer.parseInt(id);
        userService.updateTaskStatus(status, taskId);
        return "redirect:/dashboard";
    }

    @PostMapping(value = "/complete/{id}")
    public String complete(@PathVariable(name = "id")   String id){
        int taskId = Integer.parseInt(id);
        userService.markTaskCompleted(taskId);
        return "redirect:/dashboard";
    }

}
