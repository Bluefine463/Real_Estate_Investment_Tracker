// Automatically detect context path (e.g., '/rib')
const contextPath = window.location.pathname.split('/')[1];
const baseUrl = `/${contextPath}`;


const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');

loginBtn.addEventListener('click', () => {
    loginForm.classList.add('active');
    registerForm.classList.remove('active');
    loginBtn.classList.add('active');
    registerBtn.classList.remove('active');
});

registerBtn.addEventListener('click', () => {
    registerForm.classList.add('active');
    loginForm.classList.remove('active');
    registerBtn.classList.add('active');
    loginBtn.classList.remove('active');
});

function showLogin() {
    document.getElementById("formSlide").style.transform = "translateX(0%)";
    document.querySelectorAll(".toggle-btn")[0].classList.add("active");
    document.querySelectorAll(".toggle-btn")[1].classList.remove("active");
  }
  
//   function showRegister() {
//     document.getElementById("formSlide").style.transform = "translateX(-50%)";
//     document.querySelectorAll(".toggle-btn")[0].classList.remove("active");
//     document.querySelectorAll(".toggle-btn")[1].classList.add("active");
//   }
  

// Login form submission

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = loginForm.querySelector('input[name="username"]');
    const password = loginForm.querySelector('input[name="password"]');
    try{
    const response = await fetch(`${baseUrl}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
          username: username.value,
          password: password.value
        })
      });
      const data = await response.json();
        if (data) {
            alert("Login Successful!");
            // Redirect if needed: window.location.href = `${baseUrl}/dashboard`;
        } else {
            alert("Invalid credentials.");
        }
    } catch (err) {
        alert("Login Error: " + err.message);
    }
});
      

// Register form submission
registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = registerForm.querySelector('input[name="username"]');
    const password = registerForm.querySelector('input[name="password"]');
    const email = registerForm.querySelector('input[name="email"]');
    const phoneno = registerForm.querySelector('input[name="phoneno"]');
    try{
    const response = await fetch(`${baseUrl}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
          username: username.value,
          password: password.value,
          email: email.value,
          phoneno: phoneno.value
        })
      });
      
      const data = await response.json();
      if (data) {
          alert("Registration Successful!");
          showLogin(); // auto switch to login form
      } else {
          alert("Registration failed.");
      }
  } catch (err) {
      alert("Registration Error: " + err.message);
  }
});