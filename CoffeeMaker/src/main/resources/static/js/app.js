// Each time a page loads, initialize page buttons with event listeners
$(document).ready(function () {
    $('.updateInventory').on('click', function (event) {
        event.preventDefault(); // Prevents the default behavior of the button
        performInventoryCheckUpdate();
    });

    $('.addRecipe').on('click', function (event) {
        event.preventDefault();
        performInventoryCheck();
    });

    $('.makeCoffee').on('click', function (event) {
        event.preventDefault();
        ensureRecipeMake();
    });
    
    $('.deleteRecipe').on('click', function (event) {
        event.preventDefault();
        ensureRecipeDelete();
    });

    $('.editRecipe').on('click', function (event) {
        event.preventDefault();
        performInventoryCheckEdit();
    });
});

// 
// Dynamic modals
// 

function show500Error(xhrMessage) {
    $('#errorModal').modal({
        closable: false,
        blurring: true,
        title: 'Internal server error occurred (500)',
        content: xhrMessage + '- please try again later.',
    }).modal('show');
}

function showGenericError(xhrMessage) {
    $('#errorModal').modal({
        closable: false,
        blurring: true,
        title: 'An error occurred',
        content: xhrMessage,
    }).modal('show');
}

function recipeCap() {
    $('#deleteRecipeModal').modal({
        title: 'Recipe limit (3) reached',
        content: 'To add another recipe, you must first delete an existing one.',
    }).modal('show');
}

function noRecipes() {
    $('#noRecipesModal').modal({
        title: 'No recipes found in inventory',
        content: 'You must first add a recipe.',
    }).modal('show');
}

function recipeNoIngredients() {
    $('#addIngredientModal').modal({
        title: 'No ingredients in inventory',
        content: 'To add a recipe, you must add an ingredient to the inventory.',
    }).modal('show');
}

function noIngredients() {
    $('#addIngredientModal').modal({
        title: 'No ingredients in inventory',
        content: 'To update the inventory, you must have ingredients in the inventory.',
    }).modal('show');
}

function handleAPIError(xhr, status, error) {
    console.error("XHR:", xhr);
    console.error("XHR STATUS CODE:", xhr.status);
    console.error("STATUS:", status);

    if (xhr.status === 500) {
        console.error("Error 500:", status, error);
        show500Error(xhr.responseJSON.message);
    } else {
        showGenericError(xhr.responseJSON.message);
    }
}

// 
// Logic checks (used by event listeners)
// 

// Ensure there is at least one Ingredient in the inventory.
// 
// Following this check, calls performRecipeCheck to ensure we haven't
// exceeded the max number of allowed Recipes.
function performInventoryCheck() {
    $.get("/api/v1/inventory")
        .done(function (inventoryResponse) {
            if (inventoryResponse.ingredients.length < 1) {
                recipeNoIngredients();
            } else {
                performRecipeCheck();
            }
        })
        .fail(function (xhr, status, error) {
            handleAPIError(xhr, status, error);
        });
}

// Ensure there is at least one Ingredient in the inventory.
function performInventoryCheckUpdate() {
    $.get("/api/v1/inventory")
        .done(function (inventoryResponse) {
            if (inventoryResponse.ingredients.length < 1) {
                noIngredients();
            } else {
                window.location.href = 'inventory.html';
            }
        })
        .fail(function (xhr, status, error) {
            handleAPIError(xhr, status, error);
        });
}

// Ensure there is at least one Ingredient in the inventory.
// 
// Following this check, calls ensureRecipeEdit to ensure that a
// Recipe exists to edit.
function performInventoryCheckEdit() {
    $.get("/api/v1/inventory")
        .done(function (inventoryResponse) {
            if (inventoryResponse.ingredients.length < 1) {
                recipeNoIngredients();
            } else {
                ensureRecipeEdit();
            }
        })
        .fail(function (xhr, status, error) {
            handleAPIError(xhr, status, error);
        });
}

// If there are too many Recipes, show a modal. Otherwise, go to customrecipe.html
function performRecipeCheck() {
    $.get("/api/v1/recipes")
        .done(function (recipesData) {    
            if (recipesData.length >= 3) {
                console.log("Too many recipes");
                recipeCap();
            } else {
                window.location.href = 'customrecipe.html';
            }
        })
        .fail(function (xhr, status, error) {
            handleAPIError(xhr, status, error);
        });
}

// Ensure a Recipe exists to delete before going to deleterecipe.html
function ensureRecipeDelete() {
    $.get("/api/v1/recipes")
        .done(function (recipesData) {
            if (recipesData.length < 1) {
                console.log("No recipes");
                noRecipes();
            } else {
                window.location.href = 'deleterecipe.html';
            }
        })
        .fail(function (xhr, status, error) {
            handleAPIError(xhr, status, error);
        });
}

// Ensure a Recipe exists before going to makecoffee.html
function ensureRecipeMake() {
    $.get("/api/v1/recipes")
        .done(function (recipesData) {
            if (recipesData.length < 1) {
                console.log("No recipes");
                noRecipes();
            } else {
                window.location.href = 'makecoffee.html';
            }
        })
        .fail(function (xhr, status, error) {
            handleAPIError(xhr, status, error);
        });
}

// Ensure a Recipe exists before going to editrecipe.html
function ensureRecipeEdit() {
    $.get("/api/v1/recipes")
        .done(function (recipesData) {
            if (recipesData.length < 1) {
                console.log("No recipes");
                noRecipes();
            } else {
                window.location.href = 'editrecipe.html';
            }
        })
        .fail(function (xhr, status, error) {
            handleAPIError(xhr, status, error);
        });
}

// 
// Ingredient and Recipe object schema helpers
// (Used by customrecipe.html)
// 

// Construct an Ingredient object according to our schema
function constructIngredient(name, quantity) {
    return {
        id: null,
        name: name,
        quantity: quantity
    };
}

// Construct a Recipe object according to our scheme
function constructRecipe(ingredients, name, price) {
    return { ingredients, name, price };
}