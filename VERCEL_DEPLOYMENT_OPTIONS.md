# Vercel Deployment Options cho WebEcommerce

## ⚠️ Vấn Đề

**Vercel KHÔNG hỗ trợ Java applications!**

Dự án WebEcommerce của bạn là **Java Web Application (JSP/Servlet)** cần:
- ✅ Servlet Container (Tomcat, Jetty, etc.)
- ✅ Java Runtime Environment (JRE/JDK)
- ✅ Server-side rendering với JSP

Vercel chỉ hỗ trợ:
- ❌ Static sites
- ❌ Serverless functions (Node.js, Python, Go)
- ❌ Next.js, React, Vue, Angular
- ❌ Edge functions

**→ Không thể deploy trực tiếp Java backend lên Vercel!**

---

## 🎯 Giải Pháp

### Giải Pháp 1: Tách Frontend và Backend (Khuyến nghị)

**Kiến trúc:**
```
Frontend (Static) → Vercel
Backend (Java) → VPS/Cloud khác
```

#### Ưu điểm:
- ✅ Tận dụng Vercel cho frontend (free, CDN, fast)
- ✅ Giữ nguyên backend Java (không cần viết lại)
- ✅ Dễ scale và maintain

#### Nhược điểm:
- ⚠️ Cần 2 hosting (Vercel + VPS/Cloud)
- ⚠️ Cần cấu hình CORS
- ⚠️ Cần viết lại frontend thành SPA

#### Cách thực hiện:

**Bước 1: Tách Frontend thành SPA (React/Vue)**

1. Tạo React/Vue app mới:
```bash
# React
npx create-react-app ecommerce-frontend

# Hoặc Vue
npm create vue@latest ecommerce-frontend
```

2. Chuyển đổi JSP → React Components:
   - `web/views/customer/home.jsp` → `src/components/Home.jsx`
   - `web/views/customer/products.jsp` → `src/components/Products.jsx`
   - `web/views/customer/product-detail.jsp` → `src/components/ProductDetail.jsx`
   - etc.

3. Tạo API client để gọi backend:
```javascript
// src/api/client.js
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://your-backend.com/api';

export const api = {
  get: (endpoint) => fetch(`${API_BASE_URL}${endpoint}`),
  post: (endpoint, data) => fetch(`${API_BASE_URL}${endpoint}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
};
```

**Bước 2: Chuyển đổi Backend thành REST API**

1. Tạo REST API endpoints trong Java:
```java
// src/java/com/ecommerce/controller/api/ProductApiServlet.java
@WebServlet("/api/products")
public class ProductApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        List<Product> products = ProductDAO.getAllProducts();
        String json = new Gson().toJson(products);
        response.getWriter().write(json);
    }
}
```

2. Cấu hình CORS:
```java
// src/java/com/ecommerce/filter/CorsFilter.java
@WebFilter("/*")
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "https://your-frontend.vercel.app");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        chain.doFilter(request, response);
    }
}
```

**Bước 3: Deploy Frontend lên Vercel**

1. Tạo `vercel.json`:
```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ],
  "env": {
    "REACT_APP_API_URL": "https://your-backend.com/api"
  }
}
```

2. Deploy:
```bash
npm install -g vercel
vercel login
vercel --prod
```

**Bước 4: Deploy Backend lên VPS/Cloud**

- Xem hướng dẫn trong `DEPLOY_INET_VN.md`
- Hoặc deploy lên AWS/Azure/GCP

---

### Giải Pháp 2: Chuyển Đổi Sang Next.js (Nếu muốn dùng Vercel hoàn toàn)

**Kiến trúc:**
```
Next.js App (Full-stack) → Vercel
Database → Azure SQL / MongoDB Atlas
```

#### Ưu điểm:
- ✅ Deploy toàn bộ lên Vercel (free tier)
- ✅ Serverless functions tự động scale
- ✅ Không cần quản lý server

#### Nhược điểm:
- ⚠️ Phải viết lại toàn bộ backend bằng Node.js
- ⚠️ Mất nhiều thời gian
- ⚠️ Cần học Next.js/React

#### Cách thực hiện:

**Bước 1: Tạo Next.js App**
```bash
npx create-next-app@latest ecommerce-nextjs
cd ecommerce-nextjs
```

**Bước 2: Cấu trúc project:**
```
ecommerce-nextjs/
├── app/
│   ├── api/              # API routes (serverless functions)
│   │   ├── products/
│   │   ├── cart/
│   │   ├── orders/
│   │   └── auth/
│   ├── (customer)/       # Customer pages
│   │   ├── page.tsx      # Home
│   │   ├── products/
│   │   └── cart/
│   └── admin/            # Admin pages
├── lib/
│   ├── db.ts             # Database connection
│   └── utils.ts
└── components/
```

**Bước 3: Viết API Routes (Serverless Functions)**
```typescript
// app/api/products/route.ts
import { NextResponse } from 'next/server';
import { sql } from '@vercel/postgres'; // hoặc dùng SQL Server client

export async function GET() {
  try {
    const products = await sql`
      SELECT * FROM Products
    `;
    return NextResponse.json(products);
  } catch (error) {
    return NextResponse.json({ error: 'Failed to fetch products' }, { status: 500 });
  }
}
```

**Bước 4: Deploy lên Vercel**
```bash
vercel --prod
```

**Lưu ý:**
- Vercel có giới hạn 10s cho serverless functions (free tier)
- Cần dùng database external (Azure SQL, MongoDB Atlas, etc.)
- Cần migrate database schema sang database mới

---

### Giải Pháp 3: Chỉ Deploy Static Files lên Vercel (Không khuyến nghị)

**Kiến trúc:**
```
Static HTML/CSS/JS → Vercel
Backend API → VPS/Cloud khác
```

#### Vấn đề:
- ⚠️ JSP không thể render trên Vercel (cần server-side)
- ⚠️ Phải chuyển đổi tất cả JSP → HTML tĩnh
- ⚠️ Mất tính năng server-side rendering

**→ Không khuyến nghị vì mất quá nhiều tính năng!**

---

## 📊 So Sánh Các Giải Pháp

| Tiêu chí | Giải pháp 1: Tách FE/BE | Giải pháp 2: Next.js | Giải pháp 3: Static |
|----------|------------------------|----------------------|---------------------|
| **Thời gian** | 2-3 tuần | 1-2 tháng | 1 tuần |
| **Chi phí** | VPS + Vercel (free) | Vercel (free) | VPS + Vercel (free) |
| **Độ khó** | Trung bình | Cao | Thấp |
| **Tính năng** | Giữ nguyên | Giữ nguyên | Mất một số |
| **Performance** | Tốt | Rất tốt | Tốt |
| **Maintain** | Dễ | Dễ | Khó |

---

## 🎯 Khuyến Nghị

### Nếu bạn muốn:
- ✅ **Giữ nguyên code Java** → Chọn **Giải pháp 1**
- ✅ **Dùng Vercel hoàn toàn** → Chọn **Giải pháp 2** (nhưng phải viết lại)
- ✅ **Deploy nhanh** → **KHÔNG dùng Vercel**, deploy trực tiếp lên VPS (xem `DEPLOY_INET_VN.md`)

### Nếu bạn chỉ muốn demo nhanh:
- ✅ Deploy lên **VPS iNET.vn** (xem `DEPLOY_INET_VN.md`)
- ✅ Hoặc dùng **Railway.app**, **Render.com** (hỗ trợ Java)
- ✅ Hoặc dùng **AWS Elastic Beanstalk**, **Azure App Service** (hỗ trợ Java)

---

## 🚀 Các Platform Hỗ Trợ Java (Thay thế Vercel)

### 1. **Railway.app** ⭐ (Khuyến nghị)
- ✅ Hỗ trợ Java/Tomcat
- ✅ Free tier: $5 credit/tháng
- ✅ Deploy dễ dàng từ GitHub
- ✅ Auto SSL
- **Link**: https://railway.app

### 2. **Render.com**
- ✅ Hỗ trợ Java/Tomcat
- ✅ Free tier (có giới hạn)
- ✅ Deploy từ GitHub
- **Link**: https://render.com

### 3. **AWS Elastic Beanstalk**
- ✅ Hỗ trợ Java/Tomcat
- ✅ Free tier 12 tháng
- ✅ Auto scaling
- **Link**: https://aws.amazon.com/elasticbeanstalk

### 4. **Azure App Service**
- ✅ Hỗ trợ Java/Tomcat
- ✅ Free tier
- ✅ Tích hợp Azure SQL
- **Link**: https://azure.microsoft.com/services/app-service

### 5. **Google Cloud Run**
- ✅ Hỗ trợ Java (container)
- ✅ Pay per use
- ✅ Auto scaling
- **Link**: https://cloud.google.com/run

---

## 📝 Kết Luận

**Vercel KHÔNG phù hợp cho Java Web Application!**

**Khuyến nghị:**
1. **Nếu muốn dùng Vercel**: Chuyển sang Next.js (Giải pháp 2) hoặc tách frontend (Giải pháp 1)
2. **Nếu muốn giữ Java**: Deploy lên VPS (iNET.vn) hoặc Railway.app/Render.com
3. **Nếu muốn demo nhanh**: Dùng Railway.app hoặc Render.com (dễ hơn VPS)

**Câu hỏi?** Xem thêm:
- `DEPLOY_INET_VN.md` - Deploy lên VPS
- `DEPLOYMENT.md` - Deploy lên Cloud platforms

---

**Chúc bạn deploy thành công! 🚀**


