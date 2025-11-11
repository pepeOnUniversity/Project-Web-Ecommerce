// Three.js Effects for Ecommerce Store

// Particle Background Effect
class ParticleBackground {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        if (!this.container) return;
        
        this.scene = null;
        this.camera = null;
        this.renderer = null;
        this.particles = null;
        this.animationId = null;
        
        this.init();
    }
    
    init() {
        // Scene setup
        this.scene = new THREE.Scene();
        
        // Camera setup
        const width = this.container.clientWidth;
        const height = this.container.clientHeight;
        this.camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000);
        this.camera.position.z = 5;
        
        // Renderer setup
        this.renderer = new THREE.WebGLRenderer({ 
            alpha: true,
            antialias: true 
        });
        this.renderer.setSize(width, height);
        this.renderer.setPixelRatio(window.devicePixelRatio);
        this.container.appendChild(this.renderer.domElement);
        
        // Create particles
        this.createParticles();
        
        // Handle resize
        window.addEventListener('resize', () => this.handleResize());
        
        // Start animation
        this.animate();
    }
    
    createParticles() {
        const particleCount = 2000; // Increased for more impressive effect
        const geometry = new THREE.BufferGeometry();
        const positions = new Float32Array(particleCount * 3);
        const colors = new Float32Array(particleCount * 3);
        const sizes = new Float32Array(particleCount);
        
        const color1 = new THREE.Color(0x667eea); // Purple
        const color2 = new THREE.Color(0x764ba2); // Dark purple
        const color3 = new THREE.Color(0xf093fb); // Pink
        const color4 = new THREE.Color(0x4facfe); // Blue
        
        for (let i = 0; i < particleCount * 3; i += 3) {
            positions[i] = (Math.random() - 0.5) * 30;
            positions[i + 1] = (Math.random() - 0.5) * 30;
            positions[i + 2] = (Math.random() - 0.5) * 30;
            
            const rand = Math.random();
            let color;
            if (rand < 0.25) color = color1;
            else if (rand < 0.5) color = color2;
            else if (rand < 0.75) color = color3;
            else color = color4;
            
            colors[i] = color.r;
            colors[i + 1] = color.g;
            colors[i + 2] = color.b;
            
            sizes[i / 3] = Math.random() * 0.08 + 0.02;
        }
        
        geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
        geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3));
        geometry.setAttribute('size', new THREE.BufferAttribute(sizes, 1));
        
        const material = new THREE.PointsMaterial({
            size: 0.05,
            sizeAttenuation: true,
            vertexColors: true,
            transparent: true,
            opacity: 0.8,
            blending: THREE.AdditiveBlending,
            depthWrite: false
        });
        
        this.particles = new THREE.Points(geometry, material);
        this.scene.add(this.particles);
    }
    
    animate() {
        this.animationId = requestAnimationFrame(() => this.animate());
        
        if (this.particles) {
            this.particles.rotation.x += 0.0003;
            this.particles.rotation.y += 0.0008;
            this.particles.rotation.z += 0.0002;
            
            // Add floating animation
            const time = Date.now() * 0.0005;
            this.particles.position.y = Math.sin(time) * 0.5;
        }
        
        this.renderer.render(this.scene, this.camera);
    }
    
    handleResize() {
        const width = this.container.clientWidth;
        const height = this.container.clientHeight;
        
        this.camera.aspect = width / height;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(width, height);
    }
    
    destroy() {
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }
        if (this.renderer && this.container) {
            this.container.removeChild(this.renderer.domElement);
        }
    }
}

// 3D Hero Banner Effect
class Hero3DEffect {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        if (!this.container) return;
        
        this.scene = null;
        this.camera = null;
        this.renderer = null;
        this.meshes = [];
        this.animationId = null;
        this.mouseX = 0;
        this.mouseY = 0;
        
        this.init();
    }
    
    init() {
        // Scene setup
        this.scene = new THREE.Scene();
        
        // Camera setup
        const width = this.container.clientWidth;
        const height = this.container.clientHeight;
        this.camera = new THREE.PerspectiveCamera(50, width / height, 0.1, 1000);
        this.camera.position.z = 5;
        
        // Renderer setup
        this.renderer = new THREE.WebGLRenderer({ 
            alpha: true,
            antialias: true 
        });
        this.renderer.setSize(width, height);
        this.renderer.setPixelRatio(window.devicePixelRatio);
        this.renderer.domElement.style.position = 'absolute';
        this.renderer.domElement.style.top = '0';
        this.renderer.domElement.style.left = '0';
        this.renderer.domElement.style.zIndex = '1';
        this.container.style.position = 'relative';
        this.container.appendChild(this.renderer.domElement);
        
        // Create 3D shapes
        this.createShapes();
        
        // Mouse interaction
        this.container.addEventListener('mousemove', (e) => this.onMouseMove(e));
        
        // Handle resize
        window.addEventListener('resize', () => this.handleResize());
        
        // Start animation
        this.animate();
    }
    
    createShapes() {
        // Create more impressive floating geometric shapes
        const shapes = [
            { type: 'box', color: 0x667eea, position: [2, 1, 0], size: 0.6 },
            { type: 'sphere', color: 0x764ba2, position: [-2, -1, 0], size: 0.5 },
            { type: 'torus', color: 0xf093fb, position: [0, 2, -1], size: 0.4 },
            { type: 'octahedron', color: 0x4facfe, position: [-1.5, 1.5, 0.5], size: 0.4 },
            { type: 'tetrahedron', color: 0xf5576c, position: [1.5, -1.5, -0.5], size: 0.4 },
            { type: 'cone', color: 0x00f2fe, position: [0, -2, 0.5], size: 0.4 },
        ];
        
        shapes.forEach((shapeData, index) => {
            let geometry;
            switch(shapeData.type) {
                case 'box':
                    geometry = new THREE.BoxGeometry(shapeData.size, shapeData.size, shapeData.size);
                    break;
                case 'sphere':
                    geometry = new THREE.SphereGeometry(shapeData.size, 32, 32);
                    break;
                case 'torus':
                    geometry = new THREE.TorusGeometry(shapeData.size * 0.6, shapeData.size * 0.3, 16, 100);
                    break;
                case 'octahedron':
                    geometry = new THREE.OctahedronGeometry(shapeData.size);
                    break;
                case 'tetrahedron':
                    geometry = new THREE.TetrahedronGeometry(shapeData.size);
                    break;
                case 'cone':
                    geometry = new THREE.ConeGeometry(shapeData.size, shapeData.size * 1.5, 32);
                    break;
            }
            
            const material = new THREE.MeshPhongMaterial({
                color: shapeData.color,
                transparent: true,
                opacity: 0.7,
                shininess: 150,
                emissive: shapeData.color,
                emissiveIntensity: 0.2
            });
            
            const mesh = new THREE.Mesh(geometry, material);
            mesh.position.set(...shapeData.position);
            mesh.userData = { 
                originalPosition: [...shapeData.position],
                speed: 0.01 + index * 0.005,
                rotationSpeed: 0.005 + index * 0.002
            };
            
            // Add wireframe for extra visual appeal
            const wireframe = new THREE.LineSegments(
                new THREE.EdgesGeometry(geometry),
                new THREE.LineBasicMaterial({ color: shapeData.color, transparent: true, opacity: 0.3 })
            );
            mesh.add(wireframe);
            
            this.meshes.push(mesh);
            this.scene.add(mesh);
        });
        
        // Add ambient light
        const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
        this.scene.add(ambientLight);
        
        // Add multiple directional lights for better illumination
        const light1 = new THREE.DirectionalLight(0xffffff, 0.8);
        light1.position.set(5, 5, 5);
        this.scene.add(light1);
        
        const light2 = new THREE.DirectionalLight(0x667eea, 0.4);
        light2.position.set(-5, -5, 5);
        this.scene.add(light2);
        
        const light3 = new THREE.PointLight(0xf093fb, 0.5, 10);
        light3.position.set(0, 0, 5);
        this.scene.add(light3);
    }
    
    onMouseMove(event) {
        const rect = this.container.getBoundingClientRect();
        this.mouseX = ((event.clientX - rect.left) / rect.width) * 2 - 1;
        this.mouseY = -((event.clientY - rect.top) / rect.height) * 2 + 1;
    }
    
    animate() {
        this.animationId = requestAnimationFrame(() => this.animate());
        
        const time = Date.now() * 0.001;
        
        this.meshes.forEach((mesh, index) => {
            // Enhanced floating animation with multiple sine waves
            mesh.position.y = mesh.userData.originalPosition[1] + Math.sin(time + index) * 0.4 + Math.cos(time * 0.7 + index) * 0.2;
            mesh.position.x = mesh.userData.originalPosition[0] + Math.sin(time * 0.8 + index) * 0.3;
            mesh.position.z = mesh.userData.originalPosition[2] + Math.cos(time * 0.6 + index) * 0.3;
            
            // Enhanced rotation
            mesh.rotation.x += mesh.userData.rotationSpeed;
            mesh.rotation.y += mesh.userData.rotationSpeed * 1.2;
            mesh.rotation.z += mesh.userData.rotationSpeed * 0.8;
            
            // Mouse interaction with smoother follow
            const targetX = mesh.userData.originalPosition[0] + this.mouseX * 2.5;
            const targetY = mesh.position.y + this.mouseY * 1.5;
            mesh.position.x += (targetX - mesh.position.x) * 0.08;
            mesh.position.y += (targetY - mesh.position.y) * 0.08;
            
            // Pulsing effect
            const scale = 1 + Math.sin(time * 2 + index) * 0.1;
            mesh.scale.set(scale, scale, scale);
        });
        
        // Camera follow mouse with parallax effect
        this.camera.position.x += (this.mouseX * 0.8 - this.camera.position.x) * 0.05;
        this.camera.position.y += (this.mouseY * 0.8 - this.camera.position.y) * 0.05;
        this.camera.lookAt(this.scene.position);
        
        this.renderer.render(this.scene, this.camera);
    }
    
    handleResize() {
        const width = this.container.clientWidth;
        const height = this.container.clientHeight;
        
        this.camera.aspect = width / height;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(width, height);
    }
    
    destroy() {
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }
        if (this.renderer && this.container) {
            this.container.removeChild(this.renderer.domElement);
        }
    }
}

// 3D Product Card Hover Effect
class ProductCard3D {
    constructor(cardElement) {
        this.card = cardElement;
        this.scene = null;
        this.camera = null;
        this.renderer = null;
        this.mesh = null;
        this.animationId = null;
        
        this.init();
    }
    
    init() {
        const width = 200;
        const height = 200;
        
        // Scene
        this.scene = new THREE.Scene();
        
        // Camera
        this.camera = new THREE.PerspectiveCamera(50, width / height, 0.1, 1000);
        this.camera.position.z = 3;
        
        // Renderer
        this.renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true });
        this.renderer.setSize(width, height);
        this.renderer.domElement.style.position = 'absolute';
        this.renderer.domElement.style.top = '0';
        this.renderer.domElement.style.left = '0';
        this.renderer.domElement.style.pointerEvents = 'none';
        this.renderer.domElement.style.opacity = '0';
        this.renderer.domElement.style.transition = 'opacity 0.3s';
        
        // Create 3D box
        const geometry = new THREE.BoxGeometry(1, 1, 1);
        const material = new THREE.MeshPhongMaterial({
            color: 0x0d6efd,
            transparent: true,
            opacity: 0.3
        });
        this.mesh = new THREE.Mesh(geometry, material);
        this.scene.add(this.mesh);
        
        // Lights
        const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
        this.scene.add(ambientLight);
        const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
        directionalLight.position.set(5, 5, 5);
        this.scene.add(directionalLight);
        
        // Insert canvas
        const imageWrapper = this.card.querySelector('.product-image-wrapper');
        if (imageWrapper) {
            imageWrapper.style.position = 'relative';
            imageWrapper.appendChild(this.renderer.domElement);
        }
        
        // Hover events
        this.card.addEventListener('mouseenter', () => this.onHover());
        this.card.addEventListener('mouseleave', () => this.onLeave());
        this.card.addEventListener('mousemove', (e) => this.onMouseMove(e));
        
        this.animate();
    }
    
    onHover() {
        if (this.renderer.domElement) {
            this.renderer.domElement.style.opacity = '1';
        }
    }
    
    onLeave() {
        if (this.renderer.domElement) {
            this.renderer.domElement.style.opacity = '0';
        }
        if (this.mesh) {
            this.mesh.rotation.x = 0;
            this.mesh.rotation.y = 0;
        }
    }
    
    onMouseMove(event) {
        if (!this.mesh) return;
        
        const rect = this.card.getBoundingClientRect();
        const x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
        const y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
        
        this.mesh.rotation.y = x * 0.5;
        this.mesh.rotation.x = y * 0.5;
    }
    
    animate() {
        this.animationId = requestAnimationFrame(() => this.animate());
        
        if (this.mesh) {
            this.mesh.rotation.z += 0.005;
        }
        
        this.renderer.render(this.scene, this.camera);
    }
    
    destroy() {
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }
    }
}

// Initialize effects when DOM is ready and Three.js is loaded
function initializeThreeEffects() {
    // Check if Three.js is loaded
    if (typeof THREE === 'undefined') {
        console.warn('Three.js is not loaded. Effects will not be initialized.');
        return;
    }
    
    // Initialize particle background (if container exists)
    const particleContainer = document.getElementById('particle-background');
    if (particleContainer) {
        try {
            window.particleBackground = new ParticleBackground('particle-background');
        } catch (error) {
            console.error('Error initializing particle background:', error);
        }
    }
    
    // Initialize 3D hero effect (if container exists)
    const heroContainer = document.getElementById('hero-3d-container');
    if (heroContainer) {
        try {
            window.hero3DEffect = new Hero3DEffect('hero-3d-container');
        } catch (error) {
            console.error('Error initializing hero 3D effect:', error);
        }
    }
    
    // Initialize 3D product cards (optional - can be enabled if needed)
    // Uncomment to enable 3D effects on product cards
    /*
    document.querySelectorAll('.product-card').forEach(card => {
        try {
            new ProductCard3D(card);
        } catch (error) {
            console.error('Error initializing product card 3D effect:', error);
        }
    });
    */
}

// Wait for DOM and Three.js to be ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        // Wait a bit for Three.js to load if it's in header
        setTimeout(initializeThreeEffects, 100);
    });
} else {
    // DOM is already ready
    setTimeout(initializeThreeEffects, 100);
}

