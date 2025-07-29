import streamlit as st
from arilia.core.ollama_runner import extract_behaviours
from arilia.core.feature_normalizer import normalize_features
from arilia.core.rst_engine import classify_with_rough_set

st.title("APT Attribution Engine")

uploaded_file = st.file_uploader("Upload a threat report", type="txt")
if uploaded_file:
    text = uploaded_file.read().decode("utf-8")
    st.subheader("\U0001F4C4 Report Text")
    st.text(text)

    st.subheader("\U0001F9E0 Behaviour Extraction")
    data = extract_behaviours(text)
    st.json(data)

    st.subheader("\U0001F522 Normalized Features")
    features = normalize_features(data)
    st.json(features)

    st.subheader("\U0001F3AF Attribution Result")
    result = classify_with_rough_set(features)
    st.write(f"**Group:** {result['group']}")
    st.write(f"**Confidence:** {result['confidence']}")
    st.write(f"**Explanation:** {result['explanation']}")
