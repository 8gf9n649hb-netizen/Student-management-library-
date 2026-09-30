package com.studentmanagement.controller;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {
    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("list".equalsIgnoreCase(action) || action == null) {
            try {
                List<Student> students = studentDAO.getAllStudents();
                request.setAttribute("students", students);
                request.getRequestDispatcher("/index.html").forward(request, response);
            } catch (SQLException e) {
                throw new ServletException("Unable to load students.", e);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            switch (action == null ? "" : action.toLowerCase()) {
                case "add" -> {
                    Student student = readStudent(request);
                    studentDAO.addStudent(student);
                }
                case "update" -> {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Student student = readStudent(request);
                    student.setId(id);
                    studentDAO.updateStudent(student);
                }
                case "delete" -> {
                    int id = Integer.parseInt(request.getParameter("id"));
                    studentDAO.deleteStudent(id);
                }
                default -> {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
                    return;
                }
            }

            response.sendRedirect(request.getContextPath() + "/students?action=list");

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Student operation failed.", e);
        }
    }

    private Student readStudent(HttpServletRequest request) {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String course = request.getParameter("course");
        int age = Integer.parseInt(request.getParameter("age"));
        String phone = request.getParameter("phone");

        return new Student(name, email, course, age, phone);
    }
}
