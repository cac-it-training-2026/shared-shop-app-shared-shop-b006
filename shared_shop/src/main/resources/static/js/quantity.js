$(function() {
    // 数量プラスボタン
    $(document).on('click', '.plus_button', function() {
        var $input = $(this).parent().find('.quantity_input');
        var val = parseInt($input.val()) || 0;
        $input.val(val + 1);
    });

    // 数量マイナスボタン
    $(document).on('click', '.minus_button', function() {
        var $input = $(this).parent().find('.quantity_input');
        var val = parseInt($input.val()) || 0;

        // 買い物かご画面（class="shopping_basket"がある場合）は0まで許可
        var isBasket = $('.shopping_basket').length > 0;
        var min = isBasket ? 0 : 1;

        if (val > min) {
            $input.val(val - 1);
        }
    });

    // 直接入力時の制御
    $(document).on('change', '.quantity_input', function() {
        var val = parseInt($(this).val());
        var isBasket = $('.shopping_basket').length > 0;
        var min = isBasket ? 0 : 1;

        if (isNaN(val) || val < min) {
            $(this).val(min);
        }
    });
});
