package com.oracle.payments.accounts.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountsRequest {
	private String name;
	private String password;
	private String email;
}
