<script>
/*<![CDATA[*/
$(document).ready(function () {

    $("th[id^='aSort']").click(
        function () {
            HandleSortClick(event.target, 'aSort');
        }
    );

    $("span[id^='clearLine']").click(
        function () {
            HandleClick(event.target, 'clearLine');
        }
    );
    $("span[id^='removeLine']").click(
        function () {
            HandleClick(event.target, 'removeLine');
        }
    );

});

function HandleSortClick(param, prefix) {
    var editIndex = getIndex(prefix, param.id);
    var inputAction = $("<input>")
        .attr("type", "hidden")
        .attr("name", "sort").val("sort");
    var inputSort = document.getElementById("sortField");
    inputSort.setAttribute("value", editIndex);
    $("#expenseListForm").append(inputAction);

    $("#expenseListForm").submit();
}


function getIndex(prefix, text) {
    // gets a substring of the text, starting at the char after the length of the prefix
    var stripped = text.substr(prefix.length);
    return stripped;
}


/*]]>*/
</script>