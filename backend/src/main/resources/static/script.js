document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const signupForm = document.getElementById("signupForm");
    const gotoSignupButton = document.getElementById("gotoSignup");
    const signupButton = document.getElementById("signupButton");
    const loginButton = document.getElementById("loginButton"); // 추가된 부분

    // "회원 가입" 버튼을 클릭할 때 회원 가입 폼 표시
    gotoSignupButton.addEventListener("click", function () {
        loginForm.style.display = "none";
        signupForm.style.display = "block";
    });

    // "가입" 버튼을 클릭할 때 회원 가입 요청을 서버로 전송
    signupButton.addEventListener("click", function () {
        const signupEmail = document.getElementById("signupEmail").value;
        const signupPassword = document.getElementById("signupPassword").value;

        // 회원 가입 요청을 서버로 전송 (fetch API 사용)
        fetch("/api/signup", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email: signupEmail, password: signupPassword })
        })
            .then(response => response.text())
            .then(message => {
                // 서버에서의 응답을 처리 (예: "회원가입이 완료되었습니다.")
                alert(message);
                // 회원 가입이 성공하면 로그인 폼으로 전환
                loginForm.style.display = "block";
                signupForm.style.display = "none";
            })
            .catch(error => {
                console.error("회원 가입 중 오류 발생:", error);
                alert("회원 가입 중 오류가 발생했습니다.");
            });
    });

    // 로그인 버튼 클릭 이벤트 (추가된 부분)
    loginButton.addEventListener("click", async () => {
        const loginEmail = document.getElementById("loginEmail").value;
        const loginPassword = document.getElementById("loginPassword").value;

        // 서버로 로그인 요청을 보냅니다.
        const response = await fetch("/api/login", {
            method: "POST",
            body: JSON.stringify({ email: loginEmail, password: loginPassword }),
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (response.ok) {
            alert("로그인이 성공했습니다.");
            // 다른 로직 또는 페이지 전환을 추가할 수 있습니다.
        } else {
            alert("로그인에 실패했습니다.");
        }
    });
});

