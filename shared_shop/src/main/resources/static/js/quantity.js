$(function() {
    $('.minus-btn').click(function() {
        var input = $(this).siblings('input[type="number"]');
        var val = parseInt(input.val());
        var min = parseInt(input.attr('min'));
        if (isNaN(min)) min = 1;
        if (val > min) {
            input.val(val - 1);
        }
    });

    $('.plus-btn').click(function() {
        var input = $(this).siblings('input[type="number"]');
        var val = parseInt(input.val());
        var max = parseInt(input.attr('max'));
        if (isNaN(max) || val < max) {
            input.val(val + 1);
        }
    });
});
