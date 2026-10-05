document.addEventListener("DOMContentLoaded", function () {

    const slider = document.querySelector(".promotion-list");
    const slides = document.querySelectorAll(".promotion-item");

    const prevButton = document.querySelector(".slider-prev");
    const nextButton = document.querySelector(".slider-next");

    const dotsContainer = document.querySelector(".slider-dots");

    // 必要なHTMLがなかったら終了
    if (
        !slider ||
        slides.length === 0 ||
        !prevButton ||
        !nextButton ||
        !dotsContainer
    ) {
        console.error("スライダーに必要な要素が見つかりません。");
        return;
    }

    let currentIndex = 0;


    /* =========================
       ドットを作成
       ========================= */

    slides.forEach(function (slide, index) {

        const dot = document.createElement("button");

        dot.type = "button";
        dot.classList.add("slider-dot");

        dot.setAttribute(
            "aria-label",
            (index + 1) + "枚目のスライドを表示"
        );

        if (index === 0) {
            dot.classList.add("active");
        }

        dot.addEventListener("click", function () {

            currentIndex = index;

            moveSlider();

        });

        dotsContainer.appendChild(dot);

    });


    const dots = document.querySelectorAll(".slider-dot");


    /* =========================
       スライド移動
       ========================= */

    function moveSlider() {

        const slideWidth = slider.clientWidth;

        slider.scrollTo({
            left: slideWidth * currentIndex,
            behavior: "smooth"
        });

        updateDots();
    }


    /* =========================
       ドット更新
       ========================= */

    function updateDots() {

        dots.forEach(function (dot) {
            dot.classList.remove("active");
        });

        dots[currentIndex].classList.add("active");
    }


    /* =========================
       次へ
       ========================= */

    nextButton.addEventListener("click", function () {

        currentIndex++;

        if (currentIndex >= slides.length) {
            currentIndex = 0;
        }

        moveSlider();

    });


    /* =========================
       前へ
       ========================= */

    prevButton.addEventListener("click", function () {

        currentIndex--;

        if (currentIndex < 0) {
            currentIndex = slides.length - 1;
        }

        moveSlider();

    });

});