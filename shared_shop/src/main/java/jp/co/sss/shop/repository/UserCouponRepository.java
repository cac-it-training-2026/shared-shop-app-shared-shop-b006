package jp.co.sss.shop.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.UserCoupon;

/**
 * user_couponsテーブル用リポジトリ
 *
 * @author Jules
 */
@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {

	/**
	 * 指定されたユーザーの未使用かつ有効期限内のクーポンを取得する
	 * @param user ユーザーエンティティ
	 * @param isUsed 使用済みフラグ (0:未使用)
	 * @param currentDate 現在日付
	 * @return クーポンエンティティのリスト
	 */
	List<UserCoupon> findByUserAndIsUsedAndExpiryDateGreaterThanEqual(User user, Integer isUsed, Date currentDate);
}
