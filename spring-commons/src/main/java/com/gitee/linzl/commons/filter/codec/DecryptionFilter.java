/*
package com.gitee.linzl.commons.filter.codec;

import java.io.IOException;
import java.security.PrivateKey;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.linzl.cipher.KeyPairPathUtil;
import com.gitee.linzl.cipher.asymmetrical.AsymmetricalCipherUtil;
import com.gitee.linzl.cipher.asymmetrical.RSACipherAlgorithms;
import com.gitee.linzl.cipher.symmetric.AESCipherAlgorithms;
import com.gitee.linzl.cipher.symmetric.SymmetricCipherUtil;
import com.gitee.linzl.commons.api.BaseApi;
import com.gitee.linzl.commons.request.BaseBodyRequestWrapper;
import com.gitee.linzl.commons.request.BaseBodyResponseWrapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

*/
/**
 * 解密请求内容
 *
 * @author linzhenlie
 * @date 2020-01-06
 *//*

@Slf4j
public class DecryptionFilter implements Filter {
	@SneakyThrows
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// HttpServletRequest没有提供相关的set方法来修改body，所以需要用修饰类
		String contentType = request.getContentType();
		if (StringUtils.isNotEmpty(contentType) && contentType.contains("application/json")) {
			HttpServletRequest req = (HttpServletRequest) request;
			// 对称密钥base64 CBC
			String aesKey = req.getHeader("aes");

			byte[] privateKey = KeyPairPathUtil.getPrivateKeyFile();

			PrivateKey priKey = AsymmetricalCipherUtil.generatePrivate(Base64.decodeBase64(privateKey),
					RSACipherAlgorithms.RSA_ECB_PKCS1PADDING_1024);

			byte[] decryptAesKey = AsymmetricalCipherUtil.decrypt(Base64.decodeBase64(aesKey), priKey,
					RSACipherAlgorithms.RSA_ECB_PKCS1PADDING_1024);
			final byte[] aes = Base64.decodeBase64(decryptAesKey);

			ServletRequest requestWrapper = new BaseBodyRequestWrapper(req) {
				@Override
				public byte[] rewriteBody(String body) {
					super.rewriteBody(body);
					BaseApi baseApi = JSON.parseObject(body, BaseApi.class);
					try {
						byte[] bizContent = SymmetricCipherUtil.decrypt(Base64.decodeBase64(baseApi.getBizContent()),
								aes, AESCipherAlgorithms.AES_CBC_PKCS5PADDING_128);
						// String bizContent = AESUtil.decrypt(baseApi.getBizContent(), new
						// String(aes));
						System.out.println("bizContent==>" + new String(bizContent));
						baseApi.setBizContent(new String(bizContent));
					} catch (Exception e) {
						log.error("解密失败:", e);
						e.printStackTrace();
					}
					return JSON.toJSONString(baseApi).getBytes();
				}
			};
			request = requestWrapper;

			BaseBodyResponseWrapper responseWrapper = new BaseBodyResponseWrapper((HttpServletResponse) response) {
				@Override
				public byte[] rewriteBody() {
					byte[] body = super.rewriteBody();
					JSONObject obj = JSON.parseObject(new String(body));
					String data = String.valueOf(obj.get("data"));
					try {
						byte[] bizContent = SymmetricCipherUtil.encrypt(data.getBytes(), aes,
								AESCipherAlgorithms.AES_CBC_PKCS5PADDING_128);
						obj.fluentPut("data", Base64.encodeBase64(bizContent));
					} catch (Exception e) {
						e.printStackTrace();
					}
					return obj.toJSONString().getBytes();
				}
			};

			chain.doFilter(request, responseWrapper);
			byte[] body = responseWrapper.rewriteBody();
			response.setContentLength(body.length);
			response.getOutputStream().write(body);
		}
	}
}
*/
