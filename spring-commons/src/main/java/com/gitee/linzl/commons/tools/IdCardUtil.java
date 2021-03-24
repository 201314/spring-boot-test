package com.gitee.linzl.commons.tools;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IdCardUtil {
	@SuppressWarnings("serial")
	private static Map<String, String> cityCodeMap = new HashMap<String, String>() {
		{
			this.put("11", "北京");
			this.put("12", "天津");
			this.put("13", "河北");
			this.put("14", "山西");
			this.put("15", "内蒙古");
			this.put("21", "辽宁");
			this.put("22", "吉林");
			this.put("23", "黑龙江");
			this.put("31", "上海");
			this.put("32", "江苏");
			this.put("33", "浙江");
			this.put("34", "安徽");
			this.put("35", "福建");
			this.put("36", "江西");
			this.put("37", "山东");
			this.put("41", "河南");
			this.put("42", "湖北");
			this.put("43", "湖南");
			this.put("44", "广东");
			this.put("45", "广西");
			this.put("46", "海南");
			this.put("50", "重庆");
			this.put("51", "四川");
			this.put("52", "贵州");
			this.put("53", "云南");
			this.put("54", "西藏");
			this.put("61", "陕西");
			this.put("62", "甘肃");
			this.put("63", "青海");
			this.put("64", "宁夏");
			this.put("65", "新疆");
			this.put("71", "台湾");
			this.put("81", "香港");
			this.put("82", "澳门");
			this.put("91", "国外");
		}
	};

	// 第18位校检码
	@SuppressWarnings("serial")
	private static Map<Integer, String> verifyCode = new HashMap<Integer, String>() {
		{
			this.put(0, "1");
			this.put(1, "0");
			this.put(2, "X");
			this.put(3, "9");
			this.put(4, "8");
			this.put(5, "7");
			this.put(6, "6");
			this.put(7, "5");
			this.put(8, "4");
			this.put(9, "3");
			this.put(10, "2");
		}
	};

	// 每位加权因子
	private static int[] power = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	/**
	 * 验证所有的身份证的合法性
	 * 
	 * @param idcard
	 * @return
	 */
	public static boolean isValidatedIdcard(String idcard) {
		if (idcard.length() == 15) {
			idcard = convert15BitIdCardTo18Bit(idcard);
		}
		return isValidate18Idcard(idcard);
	}

	/**
	 * <p>
	 * 判断18位身份证的合法性
	 * </p>
	 * 根据〖中华人民共和国国家标准GB11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码， 由十七位数字本体码和一位数字校验码组成。
	 * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
	 * <p>
	 * 顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
	 * </p>
	 * <p>
	 * 1.前1、2位数字表示：所在省份的代码；
	 * 
	 * 2.第3、4位数字表示：所在城市的代码；
	 * 
	 * 3.第5、6位数字表示：所在区县的代码；
	 * 
	 * 4.第7~14位数字表示：出生年、月、日；
	 * 
	 * 5.第15、16位数字表示：所在地的派出所的代码；
	 * 
	 * 6.第17位数字表示性别：奇数表示男性，偶数表示女性；
	 * 
	 * 7.第18位数字是校检码：用来检验身份证的正确性 。校检码可以是0~9的数字，有时也用x表示。
	 * </p>
	 * <p>
	 * 第十八位数字(校验码)的计算方法为：
	 * 
	 * 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
	 * </p>
	 * <p>
	 * 2.将这17位数字和系数相乘的结果相加。
	 * </p>
	 * <p>
	 * 3.用加出来和除以11，看余数是多少？
	 * </p>
	 * 4.余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2。
	 * <p>
	 * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
	 * </p>
	 * 
	 * @param idCard
	 * @return
	 */
	private static boolean isValidate18Idcard(String idCard) {
		// 非18位为假
		if (idCard.length() != 18) {
			return false;
		}
		// 获取省份ID
		String provinceId = idCard.substring(0, 2);
		if (cityCodeMap.get(provinceId) == null) {
			return false;
		}

		// 校验出生日期
		String birthday = idCard.substring(6, 14);
		Date birthdate = null;
		try {
			birthdate = new SimpleDateFormat("yyyyMMdd").parse(birthday);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cday = Calendar.getInstance();
		// 出生日期必须在当天之前
		if (cday.getTime().getTime() < birthdate.getTime()) {
			return false;
		}

		// 获取前17位
		String idCard17 = idCard.substring(0, 17);
		// 获取第18位
		String idCard18Code = idCard.substring(17, 18);

		char[] c = null;
		// 是否都为数字
		if (NumberUtils.isDigits(idCard17)) {
			c = idCard17.toCharArray();
		}

		if (null != c) {
			int[] bit = char2Int(c);

			int sum17 = getPowerSum(bit);

			// 将和值与11取模得到余数进行校验码判断
			String checkCode = verifyCode.get(sum17);
			// 将身份证的第18位与算出来的校码进行匹配，不相等就为假
			if (null != checkCode && idCard18Code.equalsIgnoreCase(checkCode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将15位的身份证转成18位身份证
	 * 
	 * @param idCard
	 * @return
	 */
	public static String convert15BitIdCardTo18Bit(String idCard) {
		// 非15位身份证
		if (idCard.length() != 15) {
			return null;
		}

		if (NumberUtils.isDigits(idCard)) {
			// 获取出生年月日
			String birthday = idCard.substring(6, 12);
			Date birthdate = null;
			try {
				birthdate = new SimpleDateFormat("yyMMdd").parse(birthday);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar cday = Calendar.getInstance();
			cday.setTime(birthdate);

			String idCard17 = idCard.substring(0, 6) + cday.get(Calendar.YEAR) + idCard.substring(8);

			char[] c = idCard17.toCharArray();
			if (null != c) {
				// 将字符数组转为整型数组
				int[] bit = char2Int(c);

				int sum17 = getPowerSum(bit);

				// 将和值与11取模得到余数进行校验码判断
				String checkCode = verifyCode.get(sum17);
				if (null != checkCode) {
					// 将前17位与第18位校验码拼接
					return idCard17 + checkCode;
				}
			}
		}
		return null;
	}

	/**
	 * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
	 * 
	 * @param bit
	 * @return
	 */
	private static int getPowerSum(int[] bit) {
		int sum = 0;
		if (power.length != bit.length) {
			return sum;
		}

		for (int index = 0, length = bit.length; index < length; index++) {
			sum = sum + bit[index] * power[index];
		}
		return sum % 11;
	}

	/**
	 * 将字符数组转为整型数组
	 * 
	 * @param c
	 * @return
	 * @throws NumberFormatException
	 */
	public static int[] char2Int(char[] c) throws NumberFormatException {
		int[] a = new int[c.length];
		int k = 0;
		for (char temp : c) {
			a[k++] = Integer.parseInt(String.valueOf(temp));
		}
		return a;
	}

	public static void main(String[] args) {
		System.out.println(convert15BitIdCardTo18Bit("440222890916291"));
	}
}