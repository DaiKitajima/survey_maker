//htmlのロード後に実行されるjs
$(function(){
    //シンプルが押されたらシンプルを表示、高度を非表示
    $(".displaySwitch1").on("click", function(){
        //シンプルを表示
        $(".jDisplay-simple").css("display", "block");
        //高度を非表示
        $(".jDisplay-complex").css("display", "none");
    });

    //高度が押されたら高度を表示、シンプルを非表示
    $(".displaySwitch2").on("click", function(){
        //高度を表示
        $(".jDisplay-complex").css("display", "block");
        //シンプルを非表示
        $(".jDisplay-simple").css("display", "none");
    });
});