package com.oracle.payments.accounts.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity {
	
	@Id
	@Column
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column
	private String name;
	
	@Column
	private String password;
	
	@Column
	private String email;
	
	@Column
	private double accountBalance;
	
	@Column
	private Date updateddate;
	
	@Column
	private String status;
}
