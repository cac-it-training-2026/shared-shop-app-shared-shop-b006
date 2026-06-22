$(function() {
    // マイナスボタン押下時
    $('.minus_button').click(function() {
        var input = $(this).siblings('.quantity_input');
        var val = parseInt(input.val());
        if (val > 1) {
            input.val(val - 1);
            // 買い物かご画面の場合は自動で更新フォームを送信
            if ($(this).closest('.update_quantity_form').length > 0) {
                $(this).closest('.update_quantity_form').submit();
            }
        }
    });

    // プラスボタン押下時
    $('.plus_button').click(function() {
        var input = $(this).siblings('.quantity_input');
        var val = parseInt(input.val());
        var max = parseInt(input.attr('max'));
        if (isNaN(max) || val < max) {
            input.val(val + 1);
            // 買い物かご画面の場合は自動で更新フォームを送信
            if ($(this).closest('.update_quantity_form').length > 0) {
                $(this).closest('.update_quantity_form').submit();
            }
        }
    });

    // 入力値変更時（手入力）
    $('.quantity_input').change(function() {
        var val = parseInt($(this).val());
        var min = parseInt($(this).attr('min'));
        var max = parseInt($(this).attr('max'));

        if (isNaN(val) || val < min) {
            $(this).val(min);
        } else if (!isNaN(max) && val > max) {
            $(this).val(max);
        }

        // 買い物かご画面の場合は自動で更新フォームを送信
        if ($(this).closest('.update_quantity_form').length > 0) {
            $(this).closest('.update_quantity_form').submit();
        }
    });
});
