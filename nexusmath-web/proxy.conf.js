const backendUrl = process.env.NEXUSMATH_API_URL || 'http://localhost:8081';
let target;

try {
  target = new URL(backendUrl);
} catch {
  throw new Error(`NEXUSMATH_API_URL must be an absolute HTTP(S) URL; received "${backendUrl}".`);
}

if (!['http:', 'https:'].includes(target.protocol) || target.username || target.password) {
  throw new Error('NEXUSMATH_API_URL must be an absolute HTTP(S) URL without embedded credentials.');
}

module.exports = {
  '/api': {
    target: target.origin,
    secure: false,
    changeOrigin: true,
    logLevel: 'info',
  },
};
