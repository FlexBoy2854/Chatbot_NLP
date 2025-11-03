import streamlit as st
from openai import OpenAI
import os

# Initialize OpenAI client
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Streamlit UI setup
st.set_page_config(page_title="AI Chatbot using NLP", page_icon="💬", layout="centered")
st.title("💬 AI Chatbot using NLP")
st.markdown(
    "<p style='color: gray;'>This chatbot uses OpenAI's GPT model to interact intelligently with users.</p>",
    unsafe_allow_html=True
)

# Initialize session state for chat history
if "messages" not in st.session_state:
    st.session_state.messages = []

# User input box
user_input = st.text_input("Type your message here 👇", "")

# Handle sending message
if st.button("Send"):
    if user_input:
        st.session_state.messages.append({"role": "user", "content": user_input})
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=st.session_state.messages
        )
        reply = response.choices[0].message.content
        st.session_state.messages.append({"role": "assistant", "content": reply})

# Display chat history in a styled chat format
for msg in st.session_state.messages:
    if msg["role"] == "user":
        st.markdown(
            f"""
            <div style="
                background-color:#222831;
                color:#00ADB5;
                padding:12px;
                border-radius:12px;
                margin-bottom:10px;
                font-family: 'Segoe UI', sans-serif;
                font-size: 16px;">
                <b>You:</b> {msg['content']}
            </div>
            """,
            unsafe_allow_html=True,
        )
    else:
        st.markdown(
            f"""
            <div style="
                background-color:#393E46;
                color:#EEEEEE;
                padding:12px;
                border-radius:12px;
                margin-bottom:10px;
                font-family: 'Segoe UI', sans-serif;
                font-size: 16px;">
                <b>Chatbot:</b> {msg['content']}
            </div>
            """,
            unsafe_allow_html=True,
        )

