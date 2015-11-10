package com.crassirostris.cache.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-11-09
 * Time: 오후 9:48
 */
@Data
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
public class CustomObject {
	private int userNumber;
	private String userId;
	private String userName;
	private String nickName;
	private String description;
}
