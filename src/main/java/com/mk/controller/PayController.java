package com.mk.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mk.util.CommUtil;
import com.mk.util.Constants;
import com.mk.util.HttpUtil;
import com.mk.util.QRCodeUtil;
import com.mk.util.XMLUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 支付获取支付码
 * @author maming
 * @date 2017/11/29
 */
@WebServlet("/pay")
public class PayController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	final static Logger logger = LoggerFactory.getLogger(PayController.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("mch_id", Constants.MCH_ID);
			params.put("appid", Constants.APP_ID);
			params.put("notify_url", Constants.NOTIFY_URL);
			params.put("fee_type", Constants.FEE_TYPE);
			params.put("device_info", Constants.DEVICE_INFO);
			params.put("trade_type", Constants.TRADE_TYPE);
			params.put("sign_type", Constants.SIGN_TYPE);
			params.put("nonce_str", CommUtil.getNonce_str());
			params.put("out_trade_no", CommUtil.getNonce_str());
			params.put("spbill_create_ip", CommUtil.getIpAddress(request));
			params.put("body", "腾讯充值中心-QQ会员充值");
			params.put("product_id", "12");
			params.put("total_fee", "1");
			params.put("sign", CommUtil.generateSignature(params, Constants.API_KEY, "HMACSHA256"));
			String requestXML = XMLUtil.mapToXml(params);
			String resultXML = HttpUtil.postData(Constants.UFDODER_URL, requestXML);
			Map<String, String> result = XMLUtil.doXMLParse(resultXML);
			String codeURL = result.get("code_url");
			// 生成验证码
			QRCodeUtil.createQRCode(response, codeURL);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
