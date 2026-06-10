package jp.co.sss.shop.repository;

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
	 * 【追加】ログイン会員のIDに紐づく注文情報を注文日付降順で検索(一般会員機能で利用)
	 * * @param userId ログインユーザーのID
	 * @param pageable ページング情報
	 * @return 該当会員の注文エンティティのページオブジェクト
	 */
	@Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.insertDate DESC, o.id DESC")
	Page<Order> findByUserIdOrderByInsertDateDescIdDesc(@Param("userId") Integer userId, Pageable pageable);
}
