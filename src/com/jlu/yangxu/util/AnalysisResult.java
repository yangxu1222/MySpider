/* @author Xu Yang;
   @time 2013-5-11
*/
package com.jlu.yangxu.util;

import java.io.File;

import net.mindview.util.Directory;
import static net.mindview.util.Print.*;
public class AnalysisResult {


	public static void main(String[] args) {
		String detailPath = ConfigUtil.getProperty("CACHE_DETAIL_FILE_PATH");
		String listPath = ConfigUtil.getProperty("CACHE_LIST_FILE_PATH");
		String picturePath = ConfigUtil.getProperty("CACHE_PICTURE_PATH");
		String paramaPath = ConfigUtil.getProperty("CACHE_PRAMA_PATH");
		File[] files = Directory.local(paramaPath);
		print(files.length);
		File[] sumfiles = Directory.local(paramaPath, "Samsung_.*?");
		print(sumfiles.length);
	}

}
