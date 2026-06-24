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
	 * ルーレット結果を保持するクラス
	 */
	public static class RouletteResult {
		private int winningNumber;
		private int discountRate;

		public RouletteResult(int winningNumber, int discountRate) {
			this.winningNumber = winningNumber;
			this.discountRate = discountRate;
		}

		public int getWinningNumber() {
			return winningNumber;
		}

		public int getDiscountRate() {
			return discountRate;
		}
	}

	/**
	 * 当選する割引率をランダムに決定する
	 *
	 * 1等: 50% (20%)
	 * 2等: 30% (20%)
	 * 3等: 20% (20%)
	 * 4等: 10% (20%)
	 * 5等: 5% (20%)
	 *
	 * @return 当選した割引率(%)
	 */
	public int determineDiscountRate() {
		return determineRouletteResult().getDiscountRate();
	}

	/**
	 * ルーレットの結果（当選番号と割引率）を決定する
	 *
	 * @return ルーレット結果
	 */
	public RouletteResult determineRouletteResult() {
		Random rand = new Random();
		int num = rand.nextInt(100); // 0-99
		int discountRate;

		if (num < 20) {
			discountRate = 50; // 1等
		} else if (num < 40) {
			discountRate = 30; // 2等
		} else if (num < 60) {
			discountRate = 20; // 3等
		} else if (num < 80) {
			discountRate = 10; // 4等
		} else {
			discountRate = 5; // 5等
		}

		return new RouletteResult(num, discountRate);
	}
}
