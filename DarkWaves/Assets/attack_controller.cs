using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class attack_controller : MonoBehaviour {

	private bool attacking = false;
	private float attackTimer = 0;
	private float attackCooldown = 0.3f;
	private Animator animator;
	public Collider trigger;

	// Use this for initialization
	void Awake() {
		animator = GetComponent<Animator> ();
		trigger.enabled = false;
	}

	public void isAttackPressed(){
		Debug.Log ("Attack is pressed");
		attacking = true;
		trigger.enabled = true;
		attackTimer = attackCooldown;
	}
	
	// Update is called once per frame
	void Update () {
		if (!attacking) {
			trigger.enabled = false;
		}

		if (attacking) {
			if (attackTimer > 0) {
				attackTimer -= Time.deltaTime;
			} else {
				attacking = false;
				trigger.enabled = false;
			}
		}

		animator.SetBool ("isAttacking", attacking);
	}
}
