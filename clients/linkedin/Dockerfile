# Build stage
FROM node:20-slim as builder

# Set working directory
WORKDIR /app

# Install TypeScript globally
RUN npm install -g typescript

# Copy package files first to leverage Docker cache
COPY package*.json ./

# Install all dependencies (including devDependencies)
RUN npm ci

# Copy source code and TypeScript config
COPY tsconfig.json ./
COPY src/ ./src/

# Build TypeScript code
RUN tsc

# Production stage
FROM node:20-slim

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Copy node_modules from builder stage
COPY --from=builder /app/node_modules ./node_modules

# Copy built JavaScript files from builder stage
COPY --from=builder /app/dist ./dist

# Set environment variables
ENV NODE_ENV=production
ENV TEMPORAL_HOST=temporal:7233

# Create non-root user for security
RUN addgroup --system --gid 1001 nodejs \
    && adduser --system --uid 1001 nodejs

# Set ownership of app directory
RUN chown -R nodejs:nodejs /app

# Switch to non-root user
USER nodejs

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD node -e "require('http').get('http://localhost:8080/health', (res) => res.statusCode === 200 ? process.exit(0) : process.exit(1))" || exit 1

# Run the worker
CMD ["node", "dist/worker.js"] 