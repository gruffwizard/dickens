const API_URL = 'http://localhost:8080'; // Dummy API URL

document.addEventListener('DOMContentLoaded', () => {
  loadSketches();

  const postSketchButton = document.getElementById('postSketchButton');
  postSketchButton.addEventListener('click', postSketch);
});

// Function to load sketches
async function loadSketches() {
  try {
    const response = await fetch(`${API_URL}/sketches`);
    const sketches = await response.json();
    const feed = document.getElementById('feed');
    feed.innerHTML = '';
    sketches.forEach(sketch => {
      const sketchElement = document.createElement('div');
      sketchElement.classList.add('sketch');
      sketchElement.innerHTML = `
        <h3>${sketch.user}</h3>
        <p>${sketch.content}</p>
      `;
      feed.appendChild(sketchElement);
    });
  } catch (error) {
    console.error('Error loading sketches:', error);
  }
}

// Function to post a new sketch
async function postSketch() {
  const sketchInput = document.getElementById('sketchInput');
  const content = sketchInput.value.trim();
  if (!content) return;

  try {
    const response = await fetch(`${API_URL}/sketches`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ content, user: 'Current User' }),
    });

    if (response.ok) {
      loadSketches();
      sketchInput.value = '';
    } else {
      console.error('Error posting sketch:', await response.text());
    }
  } catch (error) {
    console.error('Error posting sketch:', error);
  }
}
