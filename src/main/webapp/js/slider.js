document.addEventListener("DOMContentLoaded", function () {

    const slider = document.querySelector(".promotion-list");
    const slides = document.querySelectorAll(".promotion-item");
    const prevButton = document.querySelector(".slider-prev");
    const nextButton = document.querySelector(".slider-next");
    const dotsContainer = document.querySelector(".slider-dots");

    if (
        !slider ||
        slides.length === 0 ||
        !prevButton ||
        !nextButton ||
        !dotsContainer
    ) {
        return;
    }

    let currentIndex = 0;
    let autoSlide;


    // =========================
    // ドット作成
    // =========================

    slides.forEach(function (slide, index) {

        const dot = document.createElement("button");

        dot.type = "button";
        dot.classList.add("slider-dot");

        if (index === 0) {
            dot.classList.add("active");
        }

        dot.addEventListener("click", function () {
            currentIndex = index;
            moveSlider();

            // 手動操作したらタイマーをリセット
            resetAutoSlide();
        });

        dotsContainer.appendChild(dot);
    });


    const dots = document.querySelectorAll(".slider-dot");


    // =========================
    // スライド移動
    // =========================

    function moveSlider() {

        const slideWidth = slider.clientWidth;

        slider.scrollTo({
            left: slideWidth * currentIndex,
            behavior: "smooth"
        });

        updateDots();
    }


    // =========================
    // ドット更新
    // =========================

    function updateDots() {

        dots.forEach(function (dot) {
            dot.classList.remove("active");
        });

        dots[currentIndex].classList.add("active");
    }


    // =========================
    // 次へ
    // =========================

    function nextSlide() {

        currentIndex++;

        if (currentIndex >= slides.length) {
            currentIndex = 0;
        }

        moveSlider();
    }


    // =========================
    // 右矢印
    // =========================

    nextButton.addEventListener("click", function () {

        nextSlide();
        resetAutoSlide();

    });


    // =========================
    // 左矢印
    // =========================

    prevButton.addEventListener("click", function () {

        currentIndex--;

        if (currentIndex < 0) {
            currentIndex = slides.length - 1;
        }

        moveSlider();
        resetAutoSlide();

    });


    // =========================
    // 自動スライド
    // =========================

    function startAutoSlide() {

        autoSlide = setInterval(function () {
            nextSlide();
        }, 5000); // 5秒

    }


    // =========================
    // タイマーをリセット
    // =========================

    function resetAutoSlide() {

        clearInterval(autoSlide);
        startAutoSlide();

    }


    // 自動スライド開始
    startAutoSlide();

});