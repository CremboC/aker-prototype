$(document).ready(function () {
    var $samples = $('.jqp-samples');

    $('.contained-overflow').css("height", $(document).height() - 350);

    $samples.selectableElement({
        element: 'tbody .selectable',
        singleType: true
    });

    $samples.pager({
        url: '/samples/json',
        template: '#sample-template',
        loadButton: '#load-samples',
        appendBefore: '#append-samples',
        metadata: '#metadata-samples',
        scrollLoad: false
    });

});
