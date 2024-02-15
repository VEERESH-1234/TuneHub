package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.entities.Song;
import com.example.demo.entities.Users;
import com.example.demo.services.SongService;
import com.example.demo.services.UsersService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersController
{
	@Autowired
	UsersService us;

	@Autowired
	SongService service;

	@PostMapping("/registration")
	public String addUsers(@ModelAttribute Users user)
	{
		boolean userStatus = us.emailExists(user.getEmail());
		if(userStatus == false)
		{
			us.addUser(user);
			System.out.println("User added");
		}
		else
		{
			System.out.println("User already exist");
		}
		return "home";
	}

	@PostMapping("/validate")
	public String validate(@RequestParam("email") String email,@RequestParam("password") String password,
			HttpSession session, Model model)
	{
		if(us.validateUser(email,password) == true)
		{
			String role = us.getRole(email);
			session.setAttribute("email", email);
			if(role.equals("admin"))
			{
				return "adminHome";
			}
			else
			{
				Users user = us.getUser(email);
				boolean userStatus = user.isPremium();
				List<Song> songsList = service.fetchAllSongs();
				model.addAttribute("songs", songsList);

				model.addAttribute("isPremium",userStatus);

				return "customerHome";
			}
		}
		else
		{
			return "login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session)
	{
		session.invalidate();
		return "login";
	}

}
