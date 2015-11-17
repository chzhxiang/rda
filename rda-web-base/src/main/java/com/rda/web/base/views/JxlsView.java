/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.web.base.views;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.processor.CellProcessor;
import net.sf.jxls.processor.RowProcessor;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * <P>使用jxls模板包生成excel文件</P>
 * @author lianrao
 */
public class JxlsView extends AbstractExcelView {

	
	private XLSTransformer transformer = new XLSTransformer();

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.document.AbstractExcelView#buildExcelDocument(java.util.Map, org.apache.poi.hssf.usermodel.HSSFWorkbook, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * @author lianrao
	 */
	@Override
	protected void buildExcelDocument(Map<String, Object> model,   Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		transformer.transformWorkbook(workbook, model);
	}

	public void setCellProcessor(CellProcessor cellProcessor) {
		transformer.registerCellProcessor(cellProcessor);
	}
	
	public void setRowProcessor(RowProcessor rowProcessor){
		transformer.registerRowProcessor(rowProcessor);
	}

}
