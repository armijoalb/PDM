using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class Health_controller : MonoBehaviour {

	private Animator animator;
	public bool isDead = false;
	private float maxHealth = 100f;
	private float currentHealth;
	public bool oneDead = false;

	void Awake(){
		animator = GetComponent<Animator> ();
		currentHealth = maxHealth;
		isDead = false;

	}
	
	// Update is called once per frame
	void Update () {
		animator.SetBool ("isDead", isDead);

		StartCoroutine (gameOver ());

		if (currentHealth <= 0) {
			isDead = true;
		}

	}

	IEnumerator gameOver(){
		if (currentHealth <= 0) {
			yield return new WaitForSeconds (1.5f);
			SceneManager.LoadScene ("GameOver");
		}
	}

	public void looseHealth(float healthLoosed){
		Debug.Log ("Loosing health");
		currentHealth -= healthLoosed;
		Debug.Log ("current health: " + currentHealth);
	}

	public float getCurrentHealth(){
		return currentHealth;
	}

}
