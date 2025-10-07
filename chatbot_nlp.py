from dotenv import load_dotenv
load_dotenv(override=True)
import os
import streamlit as st
from openai import OpenAI

# Load the .env file (and force Streamlit to use it)


# Create the OpenAI client using the loaded API key
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Optional: print key to confirm (for debugging only — you can remove later)
print("Loaded API Key:", os.getenv("OPENAI_API_KEY"))

# Streamlit app UI
st.title("💬 AI Chatbot using NLP")
st.markdown("This chatbot uses OpenAI GPT model to interact intelligently with users.")

# Initialize session history
if "messages" not in st.session_state:
    st.session_state.messages = []

# Input box
user_input = st.text_input("You:", "")

# Handle message sending
if st.button("Send"):
    if user_input:
        st.session_state.messages.append({"role": "user", "content": user_input})
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=st.session_state.messages
        )
        reply = response.choices[0].message.content
        st.session_state.messages.append({"role": "assistant", "content": reply})

# Display chat history
for msg in st.session_state.messages:
    if msg["role"] == "user":
        st.write(f"**You:** {msg['content']}")
    else:
        st.write(f"**Chatbot:** {msg['content']}")
