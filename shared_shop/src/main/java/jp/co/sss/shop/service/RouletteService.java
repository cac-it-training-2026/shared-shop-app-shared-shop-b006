package jp.co.sss.shop.service;

import java.util.Random;

import org.springframework.stereotype.Service;

/**
 * ルーレット機能用サービス
 *
 * @author Jules
 */
@Service
public class RouletteService {

	/**
	 * 当選する割引率をランダムに決定する
	 *
	 * 1等: 50% (1%)
	 * 2等: 30% (4%)
	 * 3等: 20% (15%)
	 * 4等: 10% (30%)
	 * 5等: 5% (50%)
	 *
	 * @return 当選した割引率(%)
	 */
	public int determineDiscountRate() {
		Random rand = new Random();
		int num = rand.nextInt(100); // 0-99

		if (num < 1) {
			return 50; // 1等
		} else if (num < 5) {
			return 30; // 2等
		} else if (num < 20) {
			return 20; // 3等
		} else if (num < 50) {
			return 10; // 4等
		} else {
			return 5; // 5等
		}
	}
}
