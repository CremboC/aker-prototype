$(document).ready(function () {

    var $groups = $('#jqp-groups');

    $groups.pager({
        url: '/groups/json/',
        template: '#group-template',
        loadButton: '#jqp-load-more-groups',
        appendBefore: '#jqp-append-before-groups'
    });

    $groups.selectableElement({
        element: 'tbody .selectable'
    });

    var $createGroup = $('#create-parent-group');

    $createGroup.group({
        modal: '#groupModal',
        confirm: '#modalConfirm',
        mode: 'groups',
        template: '#modal-group-template'
    });

    $groups.on('element.selected', function (event) {
        if (event.count > 0) {
            $createGroup.removeClass('hidden');
        } else {
            $createGroup.addClass('hidden');
        }
    });
});
