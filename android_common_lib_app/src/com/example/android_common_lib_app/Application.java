package com.example.android_common_lib_app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.util.Log;

import com.arista.enginetest.ParsingEngine14111;

public class Application extends android.app.Application {

	public static String msg = "test";
	public String msg_test = "msg_test"; 
	
	Element root;
	
	@Override
	public void onCreate() {
		Application.msg = "test02";
		
		//String xmlData = "<?xml version=\"1.0\" encoding=\"euc-kr\"?><xresults><h_msg_cd>IRZ000001</h_msg_cd><h_msg_txt><![CDATA[정상적으로 조회 되었습니다.]]></h_msg_txt><h_sale_dt>20121114</h_sale_dt><h_sale_tm>183046</h_sale_tm><h_tk_knd_cd>13</h_tk_knd_cd><h_tk_knd_nm>일반1개월왕복</h_tk_knd_nm><h_tk_stt_cd>02</h_tk_stt_cd><h_tk_stt_nm><![CDATA[인쇄완료]]></h_tk_stt_nm><h_tot_rcvd_amt>00074800</h_tot_rcvd_amt><h_tot_disc_amt>00000000</h_tot_disc_amt><h_stl_dt>20121114</h_stl_dt><h_stl_tm>183044</h_stl_tm><h_mlg_apl_flg></h_mlg_apl_flg><h_mb_disc_apl_flg></h_mb_disc_apl_flg><h_stl_crd_no>9445410966603008</h_stl_crd_no><h_apv_no>30008865</h_apv_no><h_stl_tp_cd>02</h_stl_tp_cd><h_stl_tp_nm>신용</h_stl_tp_nm><h_rsv_disc_crd_knd_nm><![CDATA[]]></h_rsv_disc_crd_knd_nm><h_rsv_disc_crd_no></h_rsv_disc_crd_no><h_rsv_dt>20121114</h_rsv_dt><h_rsv_tm>183044</h_rsv_tm><h_mb_crd_no>0360455005</h_mb_crd_no><h_buy_ps_nm>홍준호</h_buy_ps_nm><h_abrd_ps_nm>홍준호</h_abrd_ps_nm><h_abrd_ps_ssn>7610211000000</h_abrd_ps_ssn><h_acpt_ps_nm></h_acpt_ps_nm><h_acpt_ps_rrn></h_acpt_ps_rrn><h_abrd_ps_age>36세</h_abrd_ps_age><h_abrd_ps_sex>남</h_abrd_ps_sex><h_dlay_trn_flg>N</h_dlay_trn_flg><h_dlay_fare_pymt_dt_flg>N</h_dlay_fare_pymt_dt_flg><h_dlay_fare_pymt_flg>N</h_dlay_fare_pymt_flg><h_tel_ret_rcp_flg>N</h_tel_ret_rcp_flg><h_tel_ret_pymt_flg>N</h_tel_ret_pymt_flg><h_tel_ret_pymt_dt_flg>N</h_tel_ret_pymt_dt_flg><h_tel_ret_rcp_abl_flg>N</h_tel_ret_rcp_abl_flg><h_trn_running_flg>N</h_trn_running_flg><h_del_tk_flg>N</h_del_tk_flg><h_seat_info_cnt>001</h_seat_info_cnt><h_schd_tk_knd_cd>04</h_schd_tk_knd_cd><h_compa_nm></h_compa_nm><h_compa_brth></h_compa_brth><h_compa_sex_dv></h_compa_sex_dv><h_ret_flg>N</h_ret_flg><h_dlay_flg>N</h_dlay_flg><h_dtour>-</h_dtour><h_guide><![CDATA[토,일,공휴일 사용불가]]></h_guide><seat_infos><seat_info><h_dpt_rs_stn_cd>0256</h_dpt_rs_stn_cd><h_dpt_rs_stn_nm>평내호평</h_dpt_rs_stn_nm><h_dpt_dt>20121115</h_dpt_dt><h_dpt_tm>064600</h_dpt_tm><h_arv_rs_stn_cd>0104</h_arv_rs_stn_cd><h_arv_rs_stn_nm>용산</h_arv_rs_stn_nm><h_arv_dt>20121214</h_arv_dt><h_arv_tm>073000</h_arv_tm><h_trn_clsf_cd>09</h_trn_clsf_cd><h_trn_clsf_nm>ITX-청춘</h_trn_clsf_nm><h_trn_no>2002</h_trn_no><h_psrm_cl_cd>1</h_psrm_cl_cd><h_psrm_cl_nm>일반실</h_psrm_cl_nm><h_srcar_no>00</h_srcar_no><h_seat_no>0</h_seat_no><h_psg_tp_cd>1</h_psg_tp_cd><h_psg_tp_nm>어른</h_psg_tp_nm><h_frbs_cd></h_frbs_cd><h_disc_cd_nm></h_disc_cd_nm><h_seat_att_cd_1></h_seat_att_cd_1><h_seat_att_cd_2></h_seat_att_cd_2><h_seat_att_cd_3></h_seat_att_cd_3><h_seat_att_cd_4></h_seat_att_cd_4><h_seat_att_cd_5></h_seat_att_cd_5><h_seat_att_cd_6></h_seat_att_cd_6><h_std_seat_prc>00000000</h_std_seat_prc><h_std_seat_fare>00000000</h_std_seat_fare><h_rcvd_prc>00074800</h_rcvd_prc><h_rcvd_fare>00000000</h_rcvd_fare><h_psg_prc_disc_amt>00000000</h_psg_prc_disc_amt><h_psg_fare_disc_amt>00000000</h_psg_fare_disc_amt><h_pbl_prc_disc_amt>00000000</h_pbl_prc_disc_amt><h_pbl_fare_disc_amt>00000000</h_pbl_fare_disc_amt><h_bz1_prc_disc_amt>00000000</h_bz1_prc_disc_amt><h_bz1_fare_disc_amt>00000000</h_bz1_fare_disc_amt><h_bz2_prc_disc_amt>00000000</h_bz2_prc_disc_amt><h_bz2_fare_disc_amt>00000000</h_bz2_fare_disc_amt><h_bz3_prc_disc_amt>00000000</h_bz3_prc_disc_amt><h_bz3_fare_disc_amt>00000000</h_bz3_fare_disc_amt><h_bz4_prc_disc_amt>00000000</h_bz4_prc_disc_amt><h_bz4_fare_disc_amt>00000000</h_bz4_fare_disc_amt><h_bz5_prc_disc_amt>00000000</h_bz5_prc_disc_amt><h_bz5_fare_disc_amt>00000000</h_bz5_fare_disc_amt><h_bz6_prc_disc_amt>00000000</h_bz6_prc_disc_amt><h_bz6_fare_disc_amt>00000000</h_bz6_fare_disc_amt></seat_info></seat_infos><strResult>SUCC</strResult></xresults>";
		root = getRoot();
		
		Log.d( "BLUETOOTH", serializeXml( root ));
		
		try {
			@SuppressWarnings("unused")
			ParsingEngine14111 pe = new ParsingEngine14111( this, "http://" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		@SuppressWarnings("unused")
		String tmp = msg_test;
		
		tmp = "local set";
		
		tmp = "local set22";
		
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		
		msg_test = "purehero";
		
		super.onTerminate();
	}
	
	public Element getRoot() {
		String xmlData = "<xresults><h_msg_cd>IRZ000001</h_msg_cd><h_msg_txt><![CDATA[정상적으로 조회 되었습니다.]]></h_msg_txt><h_sale_dt>20121114</h_sale_dt><h_sale_tm>183046</h_sale_tm><h_tk_knd_cd>13</h_tk_knd_cd><h_tk_knd_nm>일반1개월왕복</h_tk_knd_nm><h_tk_stt_cd>02</h_tk_stt_cd><h_tk_stt_nm><![CDATA[인쇄완료]]></h_tk_stt_nm><h_tot_rcvd_amt>00074800</h_tot_rcvd_amt><h_tot_disc_amt>00000000</h_tot_disc_amt><h_stl_dt>20121114</h_stl_dt><h_stl_tm>183044</h_stl_tm><h_mlg_apl_flg></h_mlg_apl_flg><h_mb_disc_apl_flg></h_mb_disc_apl_flg><h_stl_crd_no>9445410966603008</h_stl_crd_no><h_apv_no>30008865</h_apv_no><h_stl_tp_cd>02</h_stl_tp_cd><h_stl_tp_nm>신용</h_stl_tp_nm><h_rsv_disc_crd_knd_nm><![CDATA[]]></h_rsv_disc_crd_knd_nm><h_rsv_disc_crd_no></h_rsv_disc_crd_no><h_rsv_dt>20121114</h_rsv_dt><h_rsv_tm>183044</h_rsv_tm><h_mb_crd_no>0360455005</h_mb_crd_no><h_buy_ps_nm>홍준호</h_buy_ps_nm><h_abrd_ps_nm>홍준호님</h_abrd_ps_nm><h_abrd_ps_ssn>7610211000000</h_abrd_ps_ssn><h_acpt_ps_nm></h_acpt_ps_nm><h_acpt_ps_rrn></h_acpt_ps_rrn><h_abrd_ps_age>36세</h_abrd_ps_age><h_abrd_ps_sex>남</h_abrd_ps_sex><h_dlay_trn_flg>N</h_dlay_trn_flg><h_dlay_fare_pymt_dt_flg>N</h_dlay_fare_pymt_dt_flg><h_dlay_fare_pymt_flg>N</h_dlay_fare_pymt_flg><h_tel_ret_rcp_flg>N</h_tel_ret_rcp_flg><h_tel_ret_pymt_flg>N</h_tel_ret_pymt_flg><h_tel_ret_pymt_dt_flg>N</h_tel_ret_pymt_dt_flg><h_tel_ret_rcp_abl_flg>N</h_tel_ret_rcp_abl_flg><h_trn_running_flg>N</h_trn_running_flg><h_del_tk_flg>N</h_del_tk_flg><h_seat_info_cnt>001</h_seat_info_cnt><h_schd_tk_knd_cd>04</h_schd_tk_knd_cd><h_compa_nm></h_compa_nm><h_compa_brth></h_compa_brth><h_compa_sex_dv></h_compa_sex_dv><h_ret_flg>N</h_ret_flg><h_dlay_flg>N</h_dlay_flg><h_dtour>-</h_dtour><h_guide><![CDATA[토,일,공휴일 사용불가]]></h_guide><seat_infos><seat_info><h_dpt_rs_stn_cd>0256</h_dpt_rs_stn_cd><h_dpt_rs_stn_nm>평내호평</h_dpt_rs_stn_nm><h_dpt_dt>20121115</h_dpt_dt><h_dpt_tm>064600</h_dpt_tm><h_arv_rs_stn_cd>0104</h_arv_rs_stn_cd><h_arv_rs_stn_nm>용산</h_arv_rs_stn_nm><h_arv_dt>20121214</h_arv_dt><h_arv_tm>073000</h_arv_tm><h_trn_clsf_cd>09</h_trn_clsf_cd><h_trn_clsf_nm>ITX-청춘</h_trn_clsf_nm><h_trn_no>2002</h_trn_no><h_psrm_cl_cd>1</h_psrm_cl_cd><h_psrm_cl_nm>일반실</h_psrm_cl_nm><h_srcar_no>00</h_srcar_no><h_seat_no>0</h_seat_no><h_psg_tp_cd>1</h_psg_tp_cd><h_psg_tp_nm>어른</h_psg_tp_nm><h_frbs_cd></h_frbs_cd><h_disc_cd_nm></h_disc_cd_nm><h_seat_att_cd_1></h_seat_att_cd_1><h_seat_att_cd_2></h_seat_att_cd_2><h_seat_att_cd_3></h_seat_att_cd_3><h_seat_att_cd_4></h_seat_att_cd_4><h_seat_att_cd_5></h_seat_att_cd_5><h_seat_att_cd_6></h_seat_att_cd_6><h_std_seat_prc>00000000</h_std_seat_prc><h_std_seat_fare>00000000</h_std_seat_fare><h_rcvd_prc>00074800</h_rcvd_prc><h_rcvd_fare>00000000</h_rcvd_fare><h_psg_prc_disc_amt>00000000</h_psg_prc_disc_amt><h_psg_fare_disc_amt>00000000</h_psg_fare_disc_amt><h_pbl_prc_disc_amt>00000000</h_pbl_prc_disc_amt><h_pbl_fare_disc_amt>00000000</h_pbl_fare_disc_amt><h_bz1_prc_disc_amt>00000000</h_bz1_prc_disc_amt><h_bz1_fare_disc_amt>00000000</h_bz1_fare_disc_amt><h_bz2_prc_disc_amt>00000000</h_bz2_prc_disc_amt><h_bz2_fare_disc_amt>00000000</h_bz2_fare_disc_amt><h_bz3_prc_disc_amt>00000000</h_bz3_prc_disc_amt><h_bz3_fare_disc_amt>00000000</h_bz3_fare_disc_amt><h_bz4_prc_disc_amt>00000000</h_bz4_prc_disc_amt><h_bz4_fare_disc_amt>00000000</h_bz4_fare_disc_amt><h_bz5_prc_disc_amt>00000000</h_bz5_prc_disc_amt><h_bz5_fare_disc_amt>00000000</h_bz5_fare_disc_amt><h_bz6_prc_disc_amt>00000000</h_bz6_prc_disc_amt><h_bz6_fare_disc_amt>00000000</h_bz6_fare_disc_amt></seat_info></seat_infos><strResult>SUCC</strResult></xresults>";
		
		InputStream is = new ByteArrayInputStream(xmlData.getBytes());
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			try {
				Document doc = builder.parse( is );
				root = doc.getDocumentElement();
				is.close();
				
				return root;
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String serializeXml(Element element)
	{
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    StreamResult result = new StreamResult(buffer);

	    DOMSource source = new DOMSource(element);
	    try {
			TransformerFactory.newInstance().newTransformer().transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}

	    return new String(buffer.toByteArray());
	}
}
