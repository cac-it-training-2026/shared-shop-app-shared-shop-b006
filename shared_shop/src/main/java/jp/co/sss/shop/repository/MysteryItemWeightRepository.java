package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.MysteryItemWeight;

/**
 * mystery_item_weightsテーブル用リポジトリ
 *
 * @author SystemShared
 */
@Repository
public interface MysteryItemWeightRepository extends JpaRepository<MysteryItemWeight, Integer> {
	/**
	 * グレードを条件にウェイトを取得
	 * @param grade グレード
	 * @return ウェイト情報のリスト
	 */
	List<MysteryItemWeight> findByGrade(int grade);
}
