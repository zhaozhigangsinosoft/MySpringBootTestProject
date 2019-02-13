package cn.com.sinosoft.reins.MySpringBootTestProject.web.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.com.sinosoft.reins.MySpringBootTestProject.web.dao.RiBaoMapper;
import cn.com.sinosoft.reins.MySpringBootTestProject.web.po.RiBao;
import cn.com.sinosoft.reins.MySpringBootTestProject.web.service.RiBaoService;

@Service
public class RiBaoServiceImpl implements RiBaoService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${params.ribao.filepath}")
	private String filePath;

	@Autowired
	private RiBaoMapper riBaoMapper;

	@Override
	public String readExcel() {
		List<RiBao> riBaoList = null;
		String result = "success";
		try {
			riBaoList = this.readFile();
			if (riBaoList != null && !riBaoList.isEmpty()) {
				riBaoMapper.deleteAll();
				riBaoMapper.insertAll(riBaoList);
			}
		} catch (Exception e) {
			result = "failed";
			e.printStackTrace();
		}
		return result;
	}

	private List<RiBao> readFile() throws Exception {
		List<RiBao> riBaoList = new ArrayList<RiBao>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			ArrayList<File> fileList = this.getFiles(this.filePath);
			for (Iterator<File> iterator = fileList.iterator(); iterator.hasNext();) {
				File file = iterator.next();
				logger.info("正在处理文件：" + file.getName());

				InputStream is = new FileInputStream(file);
				try {
					ZipSecureFile.setMinInflateRatio(-1.0d);
					@SuppressWarnings("resource")
					XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
					XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
					for (int i = 2; i <= xssfSheet.getLastRowNum(); i++) {
						XSSFRow xssfRow = xssfSheet.getRow(i);
						int index = 0;
						XSSFCell name = xssfRow.getCell(index++);
						XSSFCell workDate = xssfRow.getCell(index++);
						XSSFCell taskType = xssfRow.getCell(index++);
						XSSFCell taskNo = xssfRow.getCell(index++);
						XSSFCell workContent = xssfRow.getCell(index++);
						XSSFCell workHour = xssfRow.getCell(index++);
						XSSFCell realHour = xssfRow.getCell(index++);
						XSSFCell projectName = xssfRow.getCell(index++);
						XSSFCell remark = xssfRow.getCell(index++);

						RiBao riBao = new RiBao();
						if (name == null) {
							continue;
						}
						riBao.setName(name.toString());
						if (workDate != null) {
							double value = workDate.getNumericCellValue();
							Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
							riBao.setWorkDate(sdf.parse(sdf.format(date)));
						}
						if (taskType != null) {
							riBao.setTaskType(taskType.toString());
						}
						if (taskNo != null) {
							riBao.setTaskNo(taskNo.toString());
						}
						if (workContent != null) {
							riBao.setWorkContent(workContent.toString());
						}
						try {
							riBao.setWorkHour(BigDecimal.valueOf(Double.parseDouble(workHour.toString())));
						} catch (Exception e) {
							riBao.setWorkHour(BigDecimal.valueOf(0));
						}
						try {
							riBao.setRealHour(BigDecimal.valueOf(Double.parseDouble(realHour.toString())));
						} catch (Exception e) {
							riBao.setRealHour(BigDecimal.valueOf(0));
						}
						if (projectName != null) {
							riBao.setProjectName(projectName.toString());
						}
						if (remark != null) {
							riBao.setRemark(remark.toString());
						}

						riBaoList.add(riBao);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					is.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return riBaoList;
	}

	private ArrayList<File> getFiles(String path) throws Exception {
		ArrayList<File> fileList = new ArrayList<File>();
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fileIndex : files) {
				// 如果这个文件是目录，则进行递归搜索
				if (fileIndex.isDirectory()) {
					getFiles(fileIndex.getPath());
				} else {
					// 如果文件是普通文件，则将文件句柄放入集合中
					fileList.add(fileIndex);
				}
			}
		}
		return fileList;
	}
}
