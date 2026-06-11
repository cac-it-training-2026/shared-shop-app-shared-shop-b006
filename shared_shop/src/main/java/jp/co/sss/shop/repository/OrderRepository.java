package jp.co.sss.shop.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Order;

/**
 * ordersテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository

public interface OrderRepository extends JpaRepository<Order, Integer> {

	/**
	 * 注文日付降順で注文情報すべてを検索(管理者機能で利用)
	 * @param pageable ページング情報
	 * @return 注文エンティティのページオブジェクト
	 */
	@Query("SELECT o FROM Order o ORDER BY o.insertDate DESC,o.id DESC")
	Page<Order> findAllOrderByInsertdateDescIdDesc(Pageable pageable);

	/**
	 * 2. 【修正】会員用マイページの注文一覧表示
	 * (自分の確定済 ＝ 支払方法がNULLではない注文履歴だけを降順で取得)
	 * * @param userId ログインユーザーのID
	 * @param pageable ページング情報
	 * @return 該当会員の確定済注文エンティティのページオブジェクト
	 */
	@Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.payMethod IS NOT NULL ORDER BY o.insertDate DESC, o.id DESC")
	Page<Order> findByUserIdAndPayMethodIsNotNullOrderByInsertDateDescIdDesc(@Param("userId") Integer userId,
			Pageable pageable);

	/**
	 * 3. 【追加】注文詳細表示
	 * (他人の注文や、まだ確定していないカート状態のデータをURL直打ちで見られないようにガード)
	 * * @param id 注文ID
	 * @param userId ログインユーザーのID
	 * @return 該当する確定済注文（存在しない場合は空のOptional）
	 */
	Optional<Order> findByIdAndUserIdAndPayMethodIsNotNull(Integer id, Integer userId);
}
