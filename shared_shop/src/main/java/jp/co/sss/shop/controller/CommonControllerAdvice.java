package jp.co.sss.shop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 全画面共通で使用するデータをModelへ設定するクラス。
 * サイドバーに表示するカテゴリ一覧を取得し、
 * 「categories」として全画面へ配信する。
 *
 * @author 近藤灯
 */
@ControllerAdvice
public class CommonControllerAdvice {

	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * システム内のすべての画面にカテゴリリストを配信します
	 */
	@ModelAttribute("categories")
	public List<Category> getCategories() {
		return categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED);
	}
}
