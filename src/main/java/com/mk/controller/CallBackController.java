package com.mk.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mk.util.CommUtil;
import com.mk.util.Constants;
import com.mk.util.XMLUtil;

/**
 * 支付成功回调请求
 * @author maming
 * @date 2017/11/29
 */
@WebServlet("/renotify")
public class CallBackController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	final static Logger logger = LoggerFactory.getLogger(CallBackController.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		InputStream inputStream;
		StringBuffer sb = new StringBuffer();
		inputStream = request.getInputStream();
		String s;
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		while ((s = in.readLine()) != null) {
			sb.append(s);
		}
		in.close();
		inputStream.close();

		Map<String, String> params = null;
		try {
			params = XMLUtil.doXMLParse(sb.toString());
		} catch (JDOMException e) {
			logger.error(e.getMessage());
		}

		String resXml = ""; // 反馈给微信服务器
		// 判断签名是否正确
		if (CommUtil.isTenpaySign("UTF-8", params, Constants.API_KEY)) {
			// 这里是支付成功
			if ("SUCCESS".equals((String) params.get("result_code"))) {

				// 开始执行自己的业务逻辑

//				String mch_id = params.get("mch_id");
//				String openid = params.get("openid");
//				String is_subscribe = params.get("is_subscribe");
//				String out_trade_no = params.get("out_trade_no");
//				String total_fee = params.get("total_fee");

				// 结束执行自己的业务逻辑

				logger.info("支付成功");
				// 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

			} else {
				logger.info("支付失败,错误信息：" + params.get("err_code"));
				resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
						+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			}

		} else {
			logger.info("签名验证错误");
			resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
					+ "<return_msg><![CDATA[签名验证错误]]></return_msg>" + "</xml> ";
		}

		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(resXml.getBytes());
		out.flush();
		out.close();
	}

}
