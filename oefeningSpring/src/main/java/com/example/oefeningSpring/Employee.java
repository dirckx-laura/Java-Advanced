/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.oefeningSpring;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/**
 *
 * @author laura
 */
@Data
@Entity
public class Employee {
    private @Id @GeneratedValue Long id;
    private String name;
    private String role;
    
    Employee(){}
    
    Employee(String name, String role){
        this.name = name;
        this.role = role;
    }

    
}
