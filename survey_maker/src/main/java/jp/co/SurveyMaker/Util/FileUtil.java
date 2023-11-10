package jp.co.SurveyMaker.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

/**
 * ファイル操作用Utilityクラス
 * @author d.kitajima
 *
 */
public class FileUtil {

	private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FileUtil.class);

	/** 環境依存のデリミタ */
	public static final String FILE_DIRECTORY_DELIMITER = File.separator;
	
    /**
     * ファイル・ディレクトリ削除
     *
     * @param fileName
     * @return 成功true、失敗false
     */
    public static boolean deleteFileOrDirectory(String fileName) throws Exception{
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        } else {
            return false;
        }
    }

    /**
     * ファイル削除
     *
     * @param fileName
     * @return 成功true、失敗false
     */
    public static boolean deleteFile(String fileName) throws Exception{
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    /**
     * ディレクトリ削除
     *
     * @param directory
     * @return 成功true、失敗false
     */
    public static boolean deleteDirectory(String directory) throws Exception{
        if (!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }
        File directoryFile = new File(directory);
        if (!directoryFile.exists() || !directoryFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = directoryFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            return false;
        }
        if (directoryFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
    
	/**
	 * ファイル更新
	 *
	 * ①保存されているファイルを削除
	 * ②新しいファイルをアップロード
	 * @param fileNameStr
	 * @param saveDirStr
	 * @param targetfile
	 * @throws Exception
	 */
	public static void updateTargetFile(String saveDirStr, MultipartFile targetfile) throws Exception {

		if(StringUtil.isNotEmpty(targetfile.getOriginalFilename())) {

			File fileDir = new File(saveDirStr);

			try {
				// ①保存されているファイルを削除
				//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				for(File file : fileDir.listFiles()) {
					file.delete();
				}
			} catch(Exception e) {
				logger.error("ファイル削除処理でエラーが発生しました。", e);
				throw e;
			}

			try {
				// ②imgファイルをアップロード
				//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				String fileNameStr = targetfile.getOriginalFilename();

				File fileDest = new File(fileDir + FILE_DIRECTORY_DELIMITER + fileNameStr);
				targetfile.transferTo(fileDest);

			} catch(Exception e) {
				logger.error("ファイル更新処理でエラーが発生しました。", e);
				throw e;
			}

		}

	}

	/**
	 * ファイル削除
	 * ①保存ファイルを削除
	 * ②保存ディレクトリを削除
	 *
	 * @param targetDirStr
	 * @throws Exception
	 */
	public static void deleteTargetFile(String targetDirStr) throws Exception {

		File fileDir = new File(targetDirStr);

		try {
			// ①保存されているファイルを削除
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			for(File file : fileDir.listFiles()) {
				file.delete();
			}

		} catch(Exception e) {
			logger.error("ファイル削除処理でエラーが発生しました。", e);
			throw e;
		}

		try {
			// ②保存先フォルダを削除
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			fileDir.delete();

		} catch(Exception e) {
			logger.error("ファイル保存先削除処理でエラーが発生しました。", e);
			throw e;
		}

	}

	/**
	 * ファイル登録
	 * ①保存ディレクトリチェック
	 * ②保存ディレクトリを削除
	 *
	 * @param targetDirStr 保存先ファイルパス
	 * 	@param targetFileName	保存ファイル名（拡張子不要）
	 * 	@param uploadFile	保存元ファイル
	 * @throws Exception
	 */
	public static void registTargetFile(String targetDirStr, String targetFileName, MultipartFile uploadFile) throws Exception {

		if(StringUtil.isNotEmpty(uploadFile.getOriginalFilename())) {
			String fileName = uploadFile.getOriginalFilename();
			String externalKey = fileName.substring(fileName.lastIndexOf("."));
			targetFileName = targetFileName + externalKey;
			
			File fileSaveDir = new File(targetDirStr);

			// ①保存ディレクトリ存在チェック
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if(fileSaveDir.exists()) {
				// ディレクトリが存在する場合
				try {
					// 保存先ファイルが既に存在している場合、削除
					File targetFile = new File(targetDirStr + FILE_DIRECTORY_DELIMITER + targetFileName );
					targetFile.deleteOnExit();
				} catch(Exception e) {
					logger.error("ファイル削除処理でエラーが発生しました。", e);
					throw e;
				}

			} else {
				// 存在しない場合はディレクトリ作成
				if (!fileSaveDir.mkdirs()) {
					throw new Exception("ファイルの保存ディレクトリの作成に失敗しました。");
				}
			}

			// 権限設定
			fileSaveDir.setReadable(true, false);
			fileSaveDir.setWritable(true, false);
			fileSaveDir.setExecutable(true, false);


			// ファイルアップロード
			File uploadFileDest = null;

			try {
				uploadFileDest = new File(targetDirStr + FILE_DIRECTORY_DELIMITER + targetFileName);

				uploadFile.transferTo(uploadFileDest);

				// 権限設定
				uploadFileDest.setReadable(true, false);
				uploadFileDest.setWritable(true, false);
				uploadFileDest.setExecutable(true, false);
			} catch (Exception e) {
				logger.error("ファイルアップロード処理でエラーが発生しました。", e);
				throw e;
			}
		}

	}

	/**
	 * ファイルリネーム
	 *
	 * @param targetDirStr 保存先ファイルパス
	 * 	@param targetFileName	保存ファイル名
	 * 	@param uploadFile	リネームファイル名
	 * @throws Exception
	 */
	public static void renameTargetFile(String targetDirStr, String targetFileName, String renameFileName) throws Exception {
			
			File targetFile = new File(targetDirStr + targetFileName);

			// ①保存ディレクトリ存在チェック
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if(targetFile.exists()) {
				// ファイルが存在する場合
				try {
					File renameFile = new File(targetDirStr + renameFileName);
					targetFile.renameTo(renameFile);
				} catch(Exception e) {
					logger.error("ファイルリネームにエラーが発生しました。", e);
					throw e;
				}
			} else {
				throw new Exception("ソースファイルが存在していません。");
			}
	}
	
	
	/**
	 * 引数で指定されたバイナリデータを、レスポンスのストリームに出力しダウンロード処理を実行する
	 *
	 * @param fileName ダウンロードファイルの名称
	 * @param binary ダウンロードファイルのバイナリデータ
	 * @param res レスポンスオブジェクト
	 * @throws Exception
	 */
//	public static void downLoadTargetFile(String fileName, byte[] binary, HttpServletResponse res) throws Exception {
//
//		//=============================================================
//		// レスポンスヘッダーの設定処理
//		//=============================================================
//
//		res.setContentType("apprication/octet-stream");
//
//		// ファイル名称のエンコード
//		fileName = URLEncoder.encode(fileName, "UTF-8");
//
//		res.setHeader("Content-Disposition", "attachment; filename*=utf-8''" + fileName);
//		res.setContentLength(binary.length);
//
//		//=============================================================
//		// レスポンスストリームに出力
//		//=============================================================
//		try (OutputStream os = res.getOutputStream();) {
//
//			// ファイルをストリームに書き込み
//			os.write(binary);
//			os.flush();
//
//		} catch(Exception e) {
//			logger.error("ファイル書き込み処理でエラーが発生しました。", e);
//			throw e;
//		}
//	}

	/**
	 * Windowsのファイル名禁止文字を[_]に変換する
	 *
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String forbiddenCharacterReplace(String fileName) throws Exception {

		String replaceName = "";

		// [ ]
		replaceName = fileName.replace(" ", "_");
		// [\]
		replaceName = replaceName.replace("\\", "_");
		// [/]
		replaceName = replaceName.replace("/", "_");
		// [:]
		replaceName = replaceName.replace(":", "_");
		// [*]
		replaceName = replaceName.replace("*", "_");
		// [?]
		replaceName = replaceName.replace("?", "_");
		// [<]
		replaceName = replaceName.replace("<", "_");
		// [>]
		replaceName = replaceName.replace(">", "_");
		// [|]
		replaceName = replaceName.replace("|", "_");

		return replaceName;
	}

	/**
	 * 第一引数のディレクトリに第二引数のファイルをコピーする
	 *
	 * @param targetDirStr
	 * @param oldTargetDirStr
	 * @param fileName
	 * @throws Exception
	 */
	public static void targetFileCopy(String targetDirStr, String oldTargetDirStr, String fileName) throws Exception {

		if(StringUtil.isNotEmpty(fileName)) {

			File fileSaveDir = new File(targetDirStr);

			// ①保存ディレクトリ存在チェック
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if(fileSaveDir.exists()) {
				// ディレクトリが存在する場合
				try {
					// 保存されているファイルを削除
					if(fileSaveDir.listFiles().length != 0) {

						for(File file : fileSaveDir.listFiles()) {
							file.delete();
						}
					}

				} catch(Exception e) {
					logger.error("ファイル削除処理でエラーが発生しました。", e);
					throw e;
				}

			} else {
				// 存在しない場合はディレクトリ作成
				if (!fileSaveDir.mkdirs()) {
					throw new Exception("ファイルの保存ディレクトリの作成に失敗しました。");
				}
			}

			// 権限設定
			fileSaveDir.setReadable(true, false);
			fileSaveDir.setWritable(true, false);
			fileSaveDir.setExecutable(true, false);

			// ファイルコピー
			Path oldPath = Paths.get(oldTargetDirStr + FILE_DIRECTORY_DELIMITER + fileName);
			Path newPath = Paths.get(targetDirStr + FILE_DIRECTORY_DELIMITER + fileName);

			try {
				Files.copy(oldPath, newPath);

			} catch (Exception e) {
				logger.error("ファイルアップロード処理でエラーが発生しました。", e);
				throw e;
			}

		}
	}
	
    /**
     * フォルダコピー
     *
     * @param sourceFolderPath
     * @param targetFolderPath
     */
    public static void copyFolder(String sourceFolderPath, String targetFolderPath) throws Exception{
        File sourceFolder = new File(sourceFolderPath);
        String[] sourceFilePathList = sourceFolder.list();
        File targetFolder = new File(targetFolderPath);
        //フォルダ存在チェック
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        try {
            for (String filePath : sourceFilePathList) {
                if (new File(sourceFolderPath + FILE_DIRECTORY_DELIMITER + filePath).isDirectory()) {
                    copyFolder(sourceFolderPath + FILE_DIRECTORY_DELIMITER + filePath, targetFolderPath + FILE_DIRECTORY_DELIMITER + filePath);
                }else{
                    copyFile(sourceFolderPath + FILE_DIRECTORY_DELIMITER + filePath, targetFolderPath + FILE_DIRECTORY_DELIMITER + filePath);
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }

	// ファイルコピー
    public static void copyFile(String sourceFilePath, String targetFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        File targetFile = new File(targetFilePath);
        FileInputStream inputStream = new FileInputStream(sourceFile);
        FileOutputStream outputStream = new FileOutputStream(targetFile);
        byte[] buffer = new byte[4096];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
    
    public static void foundFileDelete(File dir, String keyWords)throws Exception{
	   	 if(dir.isDirectory()){
		     String[] subFile = dir.list();
		     for(String temp : subFile){
		         File tempF = new File(dir, temp);
		         if(tempF.isDirectory())
		        	 foundFileDelete(tempF, keyWords);
		         else {
		             if(isTarget(temp, keyWords)){
		            	 deleteFile(tempF.getAbsolutePath());
		             }
		         }
		     }
		     if(dir.list().length == 0 ) deleteDirectory(dir.getAbsolutePath());
		 }
	}

	private static boolean isTarget(String imgFileName, String keyWords){
		// 拡張子 image.png
		String key = imgFileName.substring(0,imgFileName.lastIndexOf("."));
	     if(key.equals(keyWords)){
	         return true;
	     }
	     return false;
	}
}
