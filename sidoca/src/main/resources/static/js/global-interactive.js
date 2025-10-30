/**
 * SIDOCA - Global Interactive Features
 * Professional & Elegant JavaScript Enhancements
 */

// ===== INITIALIZATION =====
document.addEventListener('DOMContentLoaded', function () {
    initScrollAnimations();
    initNavbarScroll();
    initCounterAnimations();
    initTooltips();
    initSmoothScroll();
    initFormValidation();
    initLoadingStates();
});

// ===== SCROLL ANIMATIONS =====
function initScrollAnimations() {
    const reveals = document.querySelectorAll('.reveal');

    if (reveals.length === 0) return;

    const revealObserver = new IntersectionObserver(
        (entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('active');
                    revealObserver.unobserve(entry.target);
                }
            });
        },
        {
            threshold: 0.15,
        }
    );

    reveals.forEach((reveal) => {
        revealObserver.observe(reveal);
    });
}

// ===== NAVBAR SCROLL EFFECT =====
function initNavbarScroll() {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;

    let lastScroll = 0;

    window.addEventListener('scroll', () => {
        const currentScroll = window.pageYOffset;

        if (currentScroll > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }

        lastScroll = currentScroll;
    });
}

// ===== ANIMATED COUNTERS =====
function initCounterAnimations() {
    const counters = document.querySelectorAll('.counter');

    counters.forEach((counter) => {
        const target = parseInt(counter.getAttribute('data-target'));
        const duration = parseInt(counter.getAttribute('data-duration')) || 2000;
        const increment = target / (duration / 16);
        let current = 0;

        const updateCounter = () => {
            current += increment;
            if (current < target) {
                counter.textContent = Math.ceil(current).toLocaleString('id-ID');
                requestAnimationFrame(updateCounter);
            } else {
                counter.textContent = target.toLocaleString('id-ID');
            }
        };

        const observer = new IntersectionObserver((entries) => {
            if (entries[0].isIntersecting) {
                updateCounter();
                observer.unobserve(counter);
            }
        });

        observer.observe(counter);
    });
}

// ===== SMOOTH SCROLL =====
function initSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            if (href === '#' || href === '#!') return;

            const target = document.querySelector(href);
            if (target) {
                e.preventDefault();
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start',
                });
            }
        });
    });
}

// ===== TOOLTIPS =====
function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));

    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, {
                animation: true,
                delay: { show: 300, hide: 100 },
            });
        });
    }
}

// ===== FORM VALIDATION WITH VISUAL FEEDBACK =====
function initFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');

    Array.from(forms).forEach((form) => {
        form.addEventListener(
            'submit',
            (event) => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();

                    // Add shake animation to invalid fields
                    const invalidFields = form.querySelectorAll(':invalid');
                    invalidFields.forEach((field) => {
                        field.classList.add('shake');
                        setTimeout(() => field.classList.remove('shake'), 500);
                    });
                }

                form.classList.add('was-validated');
            },
            false
        );
    });
}

// ===== LOADING STATES =====
function initLoadingStates() {
    // Add loading spinner to buttons with data-loading attribute
    document.querySelectorAll('[data-loading]').forEach((button) => {
        button.addEventListener('click', function () {
            if (this.hasAttribute('data-loading-active')) return;

            this.setAttribute('data-loading-active', '');
            const originalText = this.innerHTML;
            this.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>' + (this.getAttribute('data-loading-text') || 'Loading...');
            this.disabled = true;

            // Auto-remove after 5 seconds as failsafe
            setTimeout(() => {
                this.innerHTML = originalText;
                this.disabled = false;
                this.removeAttribute('data-loading-active');
            }, 5000);
        });
    });
}

// ===== COUNTDOWN TIMER =====
function initCountdown(elementId, targetDate) {
    const countdownElement = document.getElementById(elementId);
    if (!countdownElement) return;

    const updateCountdown = () => {
        const now = new Date().getTime();
        const distance = new Date(targetDate).getTime() - now;

        if (distance < 0) {
            countdownElement.innerHTML = '<span class="badge bg-danger">Berakhir</span>';
            return;
        }

        const days = Math.floor(distance / (1000 * 60 * 60 * 24));
        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        countdownElement.innerHTML = `
            <div class="countdown-grid">
                <div class="countdown-item animate-scale-in">
                    <span class="countdown-value">${days}</span>
                    <span class="countdown-label">Hari</span>
                </div>
                <div class="countdown-item animate-scale-in" style="animation-delay: 0.1s">
                    <span class="countdown-value">${hours}</span>
                    <span class="countdown-label">Jam</span>
                </div>
                <div class="countdown-item animate-scale-in" style="animation-delay: 0.2s">
                    <span class="countdown-value">${minutes}</span>
                    <span class="countdown-label">Menit</span>
                </div>
                <div class="countdown-item animate-scale-in" style="animation-delay: 0.3s">
                    <span class="countdown-value">${seconds}</span>
                    <span class="countdown-label">Detik</span>
                </div>
            </div>
        `;

        setTimeout(updateCountdown, 1000);
    };

    updateCountdown();
}

// ===== REAL-TIME DONATION TRACKER =====
class DonationTracker {
    constructor(campaignId, options = {}) {
        this.campaignId = campaignId;
        this.updateInterval = options.updateInterval || 5000; // 5 seconds default
        this.onUpdate = options.onUpdate || (() => {});
        this.currentAmount = options.initialAmount || 0;
        this.targetAmount = options.targetAmount || 0;
        this.donorCount = options.donorCount || 0;
    }

    start() {
        this.update();
        this.intervalId = setInterval(() => this.update(), this.updateInterval);
    }

    stop() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    async update() {
        try {
            // Fetch latest donation data
            const response = await fetch(`/api/kampanye/${this.campaignId}/stats`);
            if (!response.ok) throw new Error('Failed to fetch');

            const data = await response.json();

            // Animate changes
            this.animateValue('currentAmount', this.currentAmount, data.currentAmount);
            this.animateValue('donorCount', this.donorCount, data.donorCount);

            this.currentAmount = data.currentAmount;
            this.donorCount = data.donorCount;

            this.onUpdate(data);
        } catch (error) {
            console.warn('Donation tracker update failed:', error);
        }
    }

    animateValue(key, start, end) {
        const duration = 1000;
        const startTime = performance.now();

        const animate = (currentTime) => {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);

            const current = start + (end - start) * this.easeOutQuad(progress);
            this[key] = Math.round(current);

            if (progress < 1) {
                requestAnimationFrame(animate);
            }
        };

        requestAnimationFrame(animate);
    }

    easeOutQuad(t) {
        return t * (2 - t);
    }

    getProgress() {
        return this.targetAmount > 0 ? Math.min((this.currentAmount / this.targetAmount) * 100, 100) : 0;
    }
}

// ===== PROGRESS BAR ANIMATION =====
function animateProgressBar(elementId, targetPercent, duration = 1500) {
    const progressBar = document.getElementById(elementId);
    if (!progressBar) return;

    let start = null;
    const initialWidth = 0;

    const animate = (timestamp) => {
        if (!start) start = timestamp;
        const progress = (timestamp - start) / duration;

        if (progress < 1) {
            const currentPercent = initialWidth + (targetPercent - initialWidth) * progress;
            progressBar.style.width = currentPercent + '%';
            progressBar.setAttribute('aria-valuenow', Math.round(currentPercent));
            requestAnimationFrame(animate);
        } else {
            progressBar.style.width = targetPercent + '%';
            progressBar.setAttribute('aria-valuenow', targetPercent);
        }
    };

    requestAnimationFrame(animate);
}

// ===== TOAST NOTIFICATIONS =====
function showToast(message, type = 'info', duration = 3000) {
    const toastContainer = document.getElementById('toast-container') || createToastContainer();

    const toast = document.createElement('div');
    toast.className = `toast-notification toast-${type} animate-slide-in-up`;
    toast.innerHTML = `
        <div class="toast-content">
            <i class="bi bi-${getToastIcon(type)} me-2"></i>
            <span>${message}</span>
        </div>
        <button class="toast-close" onclick="this.parentElement.remove()">
            <i class="bi bi-x"></i>
        </button>
    `;

    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
    `;
    document.body.appendChild(container);
    return container;
}

function getToastIcon(type) {
    const icons = {
        success: 'check-circle-fill',
        error: 'exclamation-circle-fill',
        warning: 'exclamation-triangle-fill',
        info: 'info-circle-fill',
    };
    return icons[type] || icons.info;
}

// ===== LAZY LOADING IMAGES =====
function initLazyLoading() {
    const lazyImages = document.querySelectorAll('img[data-src]');

    const imageObserver = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.classList.add('fade-in');
                imageObserver.unobserve(img);
            }
        });
    });

    lazyImages.forEach((img) => imageObserver.observe(img));
}

// ===== CHART INITIALIZATION =====
function initDonationChart(canvasId, data) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || typeof Chart === 'undefined') return;

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [
                {
                    label: 'Donasi',
                    data: data.values,
                    borderColor: '#3a5a40',
                    backgroundColor: 'rgba(58, 90, 64, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                },
            ],
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false,
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    cornerRadius: 8,
                },
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)',
                    },
                },
                x: {
                    grid: {
                        display: false,
                    },
                },
            },
            animation: {
                duration: 1500,
                easing: 'easeInOutQuart',
            },
        },
    });
}

// ===== EXPORT UTILITIES =====
window.SIDOCA = {
    initCountdown,
    DonationTracker,
    animateProgressBar,
    showToast,
    initLazyLoading,
    initDonationChart,
};
