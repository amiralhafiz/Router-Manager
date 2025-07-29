"""Evaluation utilities."""
from sklearn.metrics import classification_report


def evaluate(true_labels, predicted_labels):
    """Print classification report for predictions."""
    print(classification_report(true_labels, predicted_labels))
