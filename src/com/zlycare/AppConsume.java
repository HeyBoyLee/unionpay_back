package com.zlycare;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.unionpay.acp.sdk.SDKConfig;
/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class AppConsume extends HttpServlet {
	private static final long serialVersionUID = 1L;
    //���ֱ�� className.class ��־�����ȫ�ֵ� ��rootLogger ָ�����ļ���
    //Logger logger = Logger.getLogger(Test.class.getName());
    //���ָ��logger���֣����ǰ���־�������pay-log ָ������־�ļ���ȥ
    private Logger logger = Logger.getLogger("pay-log");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AppConsume() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//getServletContext().log("lihuifeng doGet log!!!");
	  
	    System.out.println("================doGet==============");
	    logger.info(("��־��Ϣ��ʼ!"));
	    logger.info("��־��Ϣ����!");
		System.out.println("huifeng.li -> doGet!!!");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.info("appConsume  -- doPost");
		System.out.println("name:"+request.getParameter("name"));
		/**
		 * ������ʼ��
		 * ��java main ��ʽ����ʱ����ÿ�ζ�ִ�м���
		 * �������webӦ�ÿ�����,�����д�ڿ�ʹ�ü����ķ�ʽд�뻺��,�����������
		 */
		SDKConfig.getConfig().loadPropertiesFromSrc();// ��classpath����acp_sdk.properties�ļ�

		/**
		 * ��װ������
		 */
		Map<String, String> data = new HashMap<String, String>();
		// �汾��
		data.put("version", "5.0.0");
		// �ַ������� Ĭ��"UTF-8"
		data.put("encoding", "UTF-8");
		// ǩ������ 01 RSA
		data.put("signMethod", "01");
		// �������� 01-����
		data.put("txnType", "01");
		// ���������� 01:�������� 02:���� 03:���ڸ���
		data.put("txnSubType", "01");
		// ҵ������
		data.put("bizType", "000201");
		// �������ͣ�07-PC��08-�ֻ�
		data.put("channelType", "08");
		// ǰ̨֪ͨ��ַ ���ؼ����뷽ʽ������
		data.put("frontUrl", "http://localhost:8080/ACPTest/acp_front_url.do");
		// ��̨֪ͨ��ַ
		data.put("backUrl", "http://222.222.222.222:8080/ACPTest/acp_back_url.do");
		// �������ͣ��̻�������0 0- �̻� �� 1�� �յ��� 2��ƽ̨�̻�
		data.put("accessType", "0");
		// �̻����룬��ĳ��Լ����̻���
		data.put("merId", "888888888888888");
		// �̻������ţ�8-40λ������ĸ
		data.put("orderId", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// ��������ʱ�䣬ȡϵͳʱ��
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// ���׽���λ��
		data.put("txnAmt", "1");
		// ���ױ���
		data.put("currencyCode", "156");
		// ���󷽱�����͸���ֶΣ���ѯ��֪ͨ�������ļ��о���ԭ������
		// data.put("reqReserved", "͸����Ϣ");
		// �����������ɲ����ͣ�����ʱ�ؼ��л���ʾ����Ϣ
		// data.put("orderDesc", "��������");

		data = UnionBase.signData(data);

		// ��������url �������ļ���ȡ
		String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();

		Map<String, String> resmap = UnionBase.submitUrl(data, requestAppUrl);

		System.out.println("������=["+data.toString()+"]");
		System.out.println("Ӧ����=["+resmap.toString()+"]");
		
	}

}
