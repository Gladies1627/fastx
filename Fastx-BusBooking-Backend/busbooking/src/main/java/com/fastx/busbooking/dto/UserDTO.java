package com.fastx.busbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
 private int id;
 private String name;
 private String email;
 private String contactNumber;
 private String gender;
 private String role;
 private String status;
}

