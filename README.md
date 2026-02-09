<h1 align="center">🐞 Bug Tracking System – Backend</h1>

<p align="center">
  A backend-driven <b>Bug Tracking System</b> built using <b>Spring Boot</b>, designed to manage the complete lifecycle of software defects
  with secure authentication and role-based access control.
</p>

<hr/>

<h2>🚀 Features</h2>
<ul>
  <li>JWT-based authentication and authorization</li>
  <li>Role-based access control for <b>Admin, Developer, and Tester</b></li>
  <li>Complete bug lifecycle management (create, assign, prioritize, resolve)</li>
  <li>Status tracking with comments and history</li>
  <li>Secure and scalable RESTful API design</li>
</ul>

<h2>🛠 Tech Stack</h2>
<ul>
  <li><b>Backend:</b> Spring Boot</li>
  <li><b>Security:</b> Spring Security, JWT</li>
  <li><b>Database:</b> MySQL</li>
  <li><b>API Docs:</b> Swagger / OpenAPI</li>
  <li><b>Build Tool:</b> Maven</li>
</ul>

<h2>🏗 System Architecture</h2>
<ul>
  <li><b>Frontend:</b> Angular Application</li>
  <li><b>Backend:</b> Spring Boot REST APIs</li>
  <li><b>Database:</b> MySQL</li>
  <li><b>Authentication:</b> Stateless JWT-based flow</li>
</ul>

<p>
  <b>Frontend Repository:</b><br/>
  <a href="https://github.com/shashank321-png/Bug-Tracker-Frontend" target="_blank">
    https://github.com/shashank321-png/Bug-Tracker-Frontend
  </a>
</p>

<h2>📌 API Overview</h2>
<p>
  The backend exposes REST APIs for user authentication, bug lifecycle management,
  status updates, and role-based access enforcement.
</p>

<p>
  Swagger UI is available for API exploration and testing.
</p>

<h2>⚙️ Getting Started</h2>

<h3>Prerequisites</h3>
<ul>
  <li>Java 17+</li>
  <li>Maven</li>
  <li>MySQL</li>
  <li>Git</li>
</ul>

<h3>Setup Instructions</h3>
<ol>
  <li>
    Clone the repository:
    <pre><code>git clone https://github.com/SahilDhanwani/BugTracking.git
cd BugTracking</code></pre>
  </li>
  <li>
    Configure MySQL database and update credentials in
    <code>application.properties</code>
  </li>
  <li>
    Build and run the application:
    <pre><code>mvn clean install
mvn spring-boot:run</code></pre>
  </li>
  <li>
    Access Swagger UI at:
    <pre><code>http://localhost:8080/swagger-ui.html</code></pre>
  </li>
</ol>

<h2>🔐 Security</h2>
<ul>
  <li>JWT-based stateless authentication</li>
  <li>Role-based authorization enforced at API level</li>
</ul>

<h2>🔮 Future Enhancements</h2>
<ul>
  <li>Email notifications for bug updates</li>
  <li>File attachment support</li>
  <li>Advanced filtering and reporting</li>
  <li>Audit logs for bug history</li>
</ul>

<h2>👤 Author</h2>
<p>
  <b>Sahil Dhanwani</b><br/>
  Backend-focused Software Engineer<br/>
  <a href="https://github.com/SahilDhanwani" target="_blank">
    https://github.com/SahilDhanwani
  </a>
</p>

<hr/>

<p align="center">
  <i>This project is built for learning and demonstration purposes.</i>
</p>
