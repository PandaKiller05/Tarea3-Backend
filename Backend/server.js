const express = require("express");
const bcrypt = require("bcrypt");
const sqlite3 = require("sqlite3").verbose();
const path = require("path");

const app = express();
const PORT = 5000;

// Middleware para leer JSON
app.use(express.json());

// Ruta del archivo SQLite
const dbPath = path.join(__dirname, "database.sqlite");

// Conexión a SQLite
const db = new sqlite3.Database(dbPath, (err) => {
  if (err) {
    console.error("Error al conectar con SQLite:", err.message);
  } else {
    console.log("Conectado a SQLite");
  }
});

// Crear tabla si no existe
db.serialize(() => {
  db.run(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      email TEXT NOT NULL UNIQUE,
      password TEXT NOT NULL
    )
  `);
});

// GET /
app.get("/", (req, res) => {
  res.status(200).json({
    message: "API activa"
  });
});

// POST /register
app.post("/register", async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({
        error: "name, email y password son obligatorios"
      });
    }

    // Verificar si el usuario ya existe
    db.get("SELECT id FROM users WHERE email = ?", [email], async (err, row) => {
      if (err) {
        return res.status(500).json({
          error: "Error al consultar la base de datos"
        });
      }

      if (row) {
        return res.status(409).json({
          error: "El usuario ya existe"
        });
      }

      try {
        const hashedPassword = await bcrypt.hash(password, 10);

        db.run(
          "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
          [name, email, hashedPassword],
          function (err) {
            if (err) {
              return res.status(500).json({
                error: "Error al registrar el usuario"
              });
            }

            return res.status(201).json({
              message: "Usuario registrado correctamente",
              user: {
                id: this.lastID,
                name,
                email
              }
            });
          }
        );
      } catch (hashError) {
        return res.status(500).json({
          error: "Error al generar el hash de la contraseña"
        });
      }
    });
  } catch (error) {
    return res.status(500).json({
      error: "Error interno del servidor"
    });
  }
});

// POST /login
app.post("/login", (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        error: "email y password son obligatorios"
      });
    }

    db.get("SELECT * FROM users WHERE email = ?", [email], async (err, user) => {
      if (err) {
        return res.status(500).json({
          error: "Error al consultar la base de datos"
        });
      }

      if (!user) {
        return res.status(401).json({
          error: "Credenciales inválidas"
        });
      }

      try {
        const isMatch = await bcrypt.compare(password, user.password);

        if (!isMatch) {
          return res.status(401).json({
            error: "Credenciales inválidas"
          });
        }

        return res.status(200).json({
          message: "Login exitoso",
          user: {
            id: user.id,
            name: user.name,
            email: user.email
          }
        });
      } catch (compareError) {
        return res.status(500).json({
          error: "Error al verificar la contraseña"
        });
      }
    });
  } catch (error) {
    return res.status(500).json({
      error: "Error interno del servidor"
    });
  }
});

// Iniciar servidor
app.listen(PORT, "0.0.0.0", () => {
  console.log(`Servidor corriendo en http://0.0.0.0:${PORT}`);
});